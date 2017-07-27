#!/bin/sh -eux

npm run build -- -app viewer -bh /viewer/ -t production -env staging
npm run build -- -app webstore -bh /webstore/ -t production -env staging