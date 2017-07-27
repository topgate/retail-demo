sudo apt-get update
sudo apt-get install openjdk-8-jdk -y
sudo apt-get install unzip -y
project=$(gcloud info | grep Project | awk '{print substr($0, 11, index($0,"]")-11)}')
gs_zip_uri="gs://"$project"/compute/storemock-package.zip"
gsutil cp $gs_zip_uri .
unzip storemock-package.zip
cd output/
mkdir master
gs_master_store_uri="gs://"$project"/dataflow/resources/store_master.csv"
gs_master_product_uri="gs://"$project"/dataflow/resources/product_master.csv"
gsutil cp $gs_master_store_uri master/
gsutil cp $gs_master_product_uri master/
# argument: endpoint, doPerSec, lambda, stddev, useTurnAroundCategory
java -jar compute-1.0-SNAPSHOT.jar https://$project.appspot.com/api/1/order 100 0.05 40 true &
