set :stages, %w(development science staging production ec2 local)
set :default_stage, "development"
set :stage_dir, "deploy"

default_run_options[:pty] = true

# This will be the name of the top-level directory under /var/www/apps
set :application, "platform-api"

# Where Capistrano should get the deployable artifacts
set :repository,  "git@github.com:AMEE/amee.platform.api.deploy.git"

# If you aren't deploying to /u/apps/#{application} on the target
# servers (which is the default), you can specify the actual location
# via the :deploy_to variable:
set :deploy_to, "/var/www/apps/#{application}"

# If you aren't using Subversion to manage your source code, specify
# your SCM below:
# set :scm, :subversion
set :scm, :git
#set :scm_command, "/usr/bin/git"

# Sudo command on remote machine
set :sudo, "/usr/bin/sudo"
set :use_sudo, false
   
# The deployment user. This should exist in the scm and on each of the deployed-to hosts
set :user, "platform-api"

# Source code and build locations
set :src_dir, "/Development/AMEE/amee.platform.api/project/amee.platform.api"
set :package_dir, "/Development/AMEE/amee.platform.api/project/amee.platform.api.deploy"

# Pick tag to deploy.
set :branch do
  default_tag = `git tag`.split("\n").last
  tag = Capistrano::CLI.ui.ask "Tag to deploy (make sure to push the tag first): [#{default_tag}] "
  tag = default_tag if tag.empty?
  tag
end

# Override Capistrano tasks
deploy.task :start, :roles => :app do
  platform.start
end

deploy.task  :stop, :roles => :app do
  platform.stop
end

deploy.task :restart, :roles => :app do
  platform.restart
end

deploy.task :migrate, :roles => :db, :only => { :primary => true } do
  #mysql.migrate
end

deploy.task :finalize_update do
  # Override the rails stuff
end
