echo '------Uploading demo1 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb demo1
ruby upload_forms.rb demo1 1