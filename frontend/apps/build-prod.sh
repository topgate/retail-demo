#!/bin/sh -eux

npm run build -- -app viewer -bh /viewer/ -t production -env prod
npm run build -- -app webstore -bh /webstore/ -t production -env prod