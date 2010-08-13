#! /bin/sh

. curl.conf

uri="3.1/definitions/472D78F6584E?method=put"
data="usages=Usage+One,Usage+Three,Usage+Other&name=Power+Stations+And+Stuff&drillDown=state,county,metroArea,city,zipCode,plantName,powWowWow"
type="json"

curl -H "Accept: application/${type}"  \
 -b .cookies  \
 --verbose  \
 -H "Host: ${platform_host_header}"  \
 -d ${data}  \
 -u ${platform_user}:${platform_password}  \
 ${scheme}://${platform_host}/${uri}