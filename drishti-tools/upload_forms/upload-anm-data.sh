echo '------Uploading bhe2 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb bhe2
ruby upload_forms.rb bhe2 jyothi

echo '------Uploading bhe4 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb bhe4
ruby upload_forms.rb bhe4 hema

echo '------Uploading klp1 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb klp1
ruby upload_forms.rb klp1 kanya

echo '------Uploading klp2 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb klp2
ruby upload_forms.rb klp2 kamala

echo '------Uploading klp3 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb klp3
ruby upload_forms.rb klp3 indira

echo '------Uploading klp4 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb klp4
ruby upload_forms.rb klp4 hema