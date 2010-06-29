#! /bin/sh

. curl.conf

uri="3/categories/F27BF795BB04/tags"
data="tag=Another_New_Tag"
type="json"

curl -H "Accept: application/${type}"  \
 -b .cookies  \
 --verbose  \
 -H "Host: ${platform_host_header}"  \
 -d ${data}  \
 -u ${platform_user}:${platform_password}  \
 ${scheme}://${platform_host}/${uri}