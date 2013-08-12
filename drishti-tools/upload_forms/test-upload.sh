echo '------Uploading demo2 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb demo2
ruby upload_forms.rb demo2 2