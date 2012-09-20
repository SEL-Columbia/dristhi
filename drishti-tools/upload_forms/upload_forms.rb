require_relative 'lib/commcare.rb'

def upload_all filenames
  filenames.each_with_index do |filename, index|
    puts "Uploading #{index} of #{filenames.size}: #{filename}"

    CommCare.new.upload File.read filename
    sleep 3
  end
end

ec_forms = Dir['output/EC_*.xml']
anc_registration_forms = Dir['output/ANC_*.xml'] + Dir['output/ANCOutOfArea_*.xml']
anc_form_prefixes_to_upload_in_order = Dir['output/ANCVisit_*.xml'].collect {|filename| filename.gsub(/(output\/ANCVisit_\d+_).*/, '\1')}.sort.uniq
anc_outcome_forms = Dir['output/ANCOutcome_*.xml']

upload_all ec_forms
upload_all anc_registration_forms

anc_form_prefixes_to_upload_in_order.each do |prefix|
  upload_all Dir[prefix + "*"]
  puts "Sleeping for 3 minutes so that Dristhi can pull in all the information for the old visits."; sleep 180
end

upload_all anc_outcome_forms
