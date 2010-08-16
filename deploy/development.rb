# The hosts where are are deploying 
role :app, "flood.amee.com"
role :db,  "flood.amee.com", :primary => true

set :application, "platform-api-dev"
set :deploy_to, "/var/www/apps/#{application}"