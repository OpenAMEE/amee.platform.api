#! /bin/sh

. curl.conf

uri="3/categories/1D95119FB149?method=put"
data="path=car&wikiName=Car&name=Car"
type="json"

curl -H "Accept: application/${type}"  \
 -b .cookies  \
 --verbose  \
 -H "Host: ${platform_host_header}"  \
 -d ${data}  \
 -u ${platform_user}:${platform_password}  \
 ${scheme}://${platform_host}/${uri}