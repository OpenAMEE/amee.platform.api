#! /bin/sh

. curl.conf

uri="3.1/categories/Ecoinvent_agricultural_means_of_production_pesticides_diuron_at_regional_storehouse_LCI_CH_kg"

curl -H "Accept: application/x.ecospold+xml"  \
 -b .cookies  \
 --verbose  \
 -H "Host: ${platform_host_header}"  \
 -u ${platform_user}:${platform_password}  \
 ${scheme}://${platform_host}/${uri}