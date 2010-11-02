#! /bin/sh

. curl.conf

uri="3.1/categories/Ecoinvent_agricultural_means_of_production_mineral_fertiliser_diammonium_phosphate_at_regional_storehouse_RER_kg"

curl -H "Accept: application/x.ecospold+xml"  \
 -b .cookies  \
 --verbose  \
 -H "Host: ${platform_host_header}"  \
 -u ${platform_user}:${platform_password}  \
 ${scheme}://${platform_host}/${uri}