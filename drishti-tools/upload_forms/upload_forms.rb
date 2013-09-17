require_relative 'lib/dristhi.rb'
require 'fileutils'

def upload_all(filenames, user, password)
  filenames.each_with_index do |filename, index|
    puts "Uploading #{index + 1} of #{filenames.size}: #{filename}"

    Dristhi.new.upload (File.read filename), user, password
    FileUtils.mv filename, 'output/DONE/'
    sleep 1
  end
end

ec_forms = Dir['output/EC*.json']
anc_forms = Dir['output/ANC_*.json']
anc_oa_forms = Dir['output/ANCOutOfArea*.json']
anc_visit_forms = Dir['output/ANCVisit*.json']
hb_test_forms = Dir['output/HbTest*.json']
ifa_forms = Dir['output/IFA*.json']
tt_forms = Dir['output/TT*.json']
do_forms = Dir['output/DO*.json']
pnc_oa_forms = Dir['output/PNCOutOfArea*.json']
pnc_visit_forms = Dir['output/PNCVisit*.json']
ppfp_forms = Dir['output/PP_FP*.json']
child_registration_ec_forms = Dir['output/Child_Registration_EC*.json']
child_immunization_forms = Dir['output/Child_Immunization*.json']
vitamin_a_dosage_forms = Dir['output/Vitamin_A*.json']

upload_all ec_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all anc_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all anc_oa_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all anc_visit_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all hb_test_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all ifa_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all tt_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all do_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all pnc_oa_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all pnc_visit_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all ppfp_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all child_registration_ec_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all child_immunization_forms, ARGV[0].to_s, ARGV[1].to_s
upload_all vitamin_a_dosage_forms, ARGV[0].to_s, ARGV[1].to_s