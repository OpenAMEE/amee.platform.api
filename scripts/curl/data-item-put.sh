#! /bin/sh

. curl.conf

uri="3/categories/87E55DA88017/items/7E2B2426C927?method=put"
data="path=aaaaa"
type="json"

curl -H "Accept: application/${type}"  \
 -b .cookies  \
 --verbose  \
 -H "Host: ${platform_host_header}"  \
 -d ${data}  \
 -u ${platform_user}:${platform_password}  \
 ${scheme}://${platform_host}/${uri}