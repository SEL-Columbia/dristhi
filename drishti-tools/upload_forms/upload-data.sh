echo '------Uploading demo1 data-------'
rm output/DONE/*.json
rm output/*.json
ruby create_forms.rb demo1
ruby upload_forms.rb demo1 1

echo "------Uploading trainers data-------"
for index in {1..2}
do
   echo "------Uploading trainer$index data-------"
   rm output/DONE/*.json
   rm output/*.json
   ruby create_forms.rb trainer$index
   ruby upload_forms.rb trainer$index $index
done

echo "------Uploading Training users data-------"
for index in {1..10}
do
   echo "------Uploading user$index data-------"
   rm output/DONE/*.json
   rm output/*.json
   ruby create_forms.rb user$index
   ruby upload_forms.rb user$index $index
done

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