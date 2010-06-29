namespace :platform do
  [:stop, :start, :restart, :status].each do |action|
    desc "#{action.to_s.capitalize} Platform"
    task action, :roles => :app do
      sudo "/etc/init.d/platform.api #{action.to_s}", :via => run_method
    end
  end
  
  desc "Ls current install"
  task :check_current, roles => :app do
    stream "ls -ld #{current_release}/.."
  end
  
  desc "Tail logfile"
  task :tail_log, roles => :app do
    stream "tail -f #{current_release}/log/wrapper.log"
  end
   
  desc "Create symlink to start script"
  task :init_d, roles => :app do
    sudo "ln -s #{current_path}/bin/amee /etc/init.d/amee && chmod 777 /etc/init.d/amee"
  end

  desc "Grep for 40* and 500 status in logs"
  task :grep_status, roles => :app do
    stream "egrep 'S:500|S:40' #{current_release}/log/wrapper.log"
  end

  #after "deploy:setup", "platform:init_d"   

end
