# The hosts where are are deploying 
role :app, "monsoon.amee.com"
role :db,  "monsoon.amee.com", :primary => true

set :application, "platform-api-dev"
set :deploy_to, "/var/www/apps/#{application}"