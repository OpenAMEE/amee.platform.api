#! /bin/sh

. curl.conf

uri="3.1/definitions/472D78F6584E/values/F04F3AFB8078?method=put"
type="xml"

curl \
 -H "Host: ${platform_host_header}"  \
 -H "Accept: application/${type}"  \
 -H "Content-Type: application/${type}"  \
 -u ${platform_user}:${platform_password}  \
 -b .cookies  \
 --verbose  \
 --data-binary @ivd.xml \
 ${scheme}://${platform_host}/${uri}