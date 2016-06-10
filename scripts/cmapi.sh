curl -X POST -H "Content-Type:application/json" -u admin:admin \
  -d '{ "items": [ { "name": "YARN", "type": "YARN" } ] }' \
  'http://50.78.10.82:7180/api/v7/clusters/examples_demo_cluster/services/examples_demo_clusterHDFS/commands/hdfsEnableNnHaservices'

