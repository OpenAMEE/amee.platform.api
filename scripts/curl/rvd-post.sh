#! /bin/sh

. curl.conf

uri="/3.1/definitions/11D3548466F2/returnvalues"
data="type=CO2&unit=kg&perUnit=month&valueDefinition=45433E48B39F"

type="json"

curl \
 -H "Host: ${platform_host_header}"  \
 -H "Accept: application/${type}"  \
 -u ${platform_user}:${platform_password}  \
 -b .cookies  \
 -d ${data} \
 --verbose  \
 ${scheme}://${platform_host}/${uri}