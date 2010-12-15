require 'fileutils'

namespace :install do
  
  desc "Package and install into the SCM"
  task :default do
    check
    prepare
    package
    deploy_to_git
    tag
  end
  
  desc "Check install dependencies"
  task :check do
    
    # Check package directory has been checked-out from Git repository
    if !File.directory?(package_dir)
      puts "ERROR: Abandoning install - #{package_dir} does not exist. Please clone the deployment Git repository before continuing."
      exit
    end
    
    # Ensure a release tag has been supplied 
    @tag_name = ENV['TAG']
    unless @tag_name
      puts "You must specify a tag for this release using TAG=name"
      exit
    end
    
    # Check that local repository contains no local modifications
    unless `git status` =~ /working directory clean/
      puts "Must have clean working directory - #{src_dir} contains local modifications."
      exit
    end

  end
  
  desc "Prepare the deployment repo"
  task :prepare do
    
    # Switch to the correct branch and update deploy repo from origin
    Dir.chdir(package_dir)
    `git checkout master`
    `git fetch`
    
    # Remove the previous install artifacts
    FileUtils.rm_r Dir.glob("#{package_dir}/*")
  end
  
  desc "Build the deployment package"  
  task :package do

    # Create bin
    puts "Creating new deployment bin directory #{package_dir}/bin"
    FileUtils.mkdir_p("#{package_dir}/bin")
    FileUtils.cp_r "#{src_dir}/server/engine/bin/.","#{package_dir}/bin"  
    
    # Create conf
    puts "Creating new deployment conf directory #{package_dir}/conf"
    FileUtils.mkdir_p("#{package_dir}/conf")
    FileUtils.cp_r "#{src_dir}/server/engine/conf/.","#{package_dir}/conf"  

    # Create lib
    puts "Creating new deployment lib directory #{package_dir}/lib"
    FileUtils.mkdir_p("#{package_dir}/lib")
    FileUtils.cp_r Dir.glob("#{src_dir}/server/target/dependency/*.jar"), "#{package_dir}/lib"  
    FileUtils.cp_r Dir.glob("#{src_dir}/server/*/target/dependency/*.jar"), "#{package_dir}/lib"  
    FileUtils.cp_r Dir.glob("#{src_dir}/server/*/target/*.jar"), "#{package_dir}/lib"  
    FileUtils.cp_r "#{src_dir}/server/engine/lib/wrapper","#{package_dir}/lib"  
  end

  desc "Send the deployment package to Git repository"
  task :deploy_to_git do
    `git add .`
    `git commit -a -m 'Install from capistrano on #{Time.now}'`
    `git push -v`
  end
  
  desc "Tag the src and deployment repositories"
  task :tag do
    unless @tag_name
      puts "You must specify a tag for this release using TAG=name"
      exit
    end
        
    # Write out the tag to file for packaging with the deployment repo.
    system("echo #{@tag_name} > VERSION.txt")
    `git add VERSION.txt`
    `git commit -a -m 'Install from capistrano on #{Time.now}'`
    `git push -v`
        
    # Tag the deployment repository
    `git tag -f -a "#{@tag_name}" -m "#{@tag_name}"`
    `git push --tags -v`
    
    # Tag the src repository
    Dir.chdir(src_dir)
    `git tag -f -a "#{@tag_name}" -m "#{@tag_name}"`
    `git push --tags -v`
  end
  
end
