require 'date'

class Forms
  def initialize mobile_worker, ec_data, anc_data, anc_visits_data, hb_tests_data, ifa_data, tt_data, pnc_data, pnc_visits_data
    @mobile_worker = mobile_worker
    @ec = ec_data
    @ancs = anc_data
    @anc_visits = anc_visits_data
    @hb_tests = hb_tests_data
    @ifas = ifa_data
    @tts = tt_data
    @pncs = pnc_data
    @pnc_visits = pnc_visits_data
  end

  def fill_for_in_area
    fill_ec_form
    fill_anc_registration_forms if has_anc?
    #fill_anc_services_forms if has_anc? and has_services?
    #fill_anc_outcome_forms if has_anc? and has_services? and has_outcome?
  end

  def fill_for_out_of_area
    fill_out_of_area_anc_registration_forms if has_anc?
    #fill_anc_services_forms if has_anc? and has_services?
    #fill_anc_outcome_forms if has_anc? and has_services? and has_outcome?
  end

  def fill_ec_form
    puts "EC: #{@ec['Wife Name']} - #{@ec['Husband Name']} - #{@ec['Entity ID']}"

    form_instance_erb = ERB.new(File.read('templates/form_instance_erb/ec_registration.json'))
    ec_registration_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

    ec = @ec
    user_id = @mobile_worker.user_id
    user_name = @mobile_worker.user_name
    form_name = "ec_registration"
    instance_id = ec['Instance ID']
    entity_id = ec['Entity ID']

    form_instance = form_instance_erb.result(binding)
    ec_registration_json = ec_registration_erb.result(binding)

    File.open("output/EC_#{ec['Entity ID']}.json", "w") do |f|
      f.puts ec_registration_json
    end
  end

  def fill_out_of_area_anc_registration_forms
    @ancs.each do |anc|

      puts "Out of area ANC registration: #{anc['Wife Name']} - #{anc['Husband Name']} - #{anc['LMP']} - #{anc['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/anc_registration_oa.json'))
      out_of_area_anc_registration_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      ec = @ec
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "anc_registration_oa"
      instance_id = anc['Instance ID']
      entity_id = anc['Entity ID']

      form_instance = form_instance_erb.result(binding)
      out_of_area_anc_registration_json = out_of_area_anc_registration_erb.result(binding)
      File.open("output/ANCOutOfArea_#{anc['Entity ID']}.json", "w") do |f|
        f.puts out_of_area_anc_registration_json
      end
    end
  end

  def fill_anc_registration_forms
    @ancs.each do |value|
      anc = get_safe_map(value)
      puts "ANC registration: #{anc['LMP']} - #{anc['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/anc_registration.json'))
      anc_registration_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      ec = @ec
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "anc_registration"
      instance_id = anc['Instance ID']
      entity_id = ec['Entity ID']

      form_instance = form_instance_erb.result(binding)
      anc_registration_json = anc_registration_erb.result(binding)
      File.open("output/ANC_#{anc['Entity ID']}.json", "w") do |f|
        f.puts anc_registration_json
      end
    end
  end

  def fill_anc_visits_forms
    @anc_visits.each do |anc_visit|
      puts "ANC Visit: #{anc_visit['Wife Name']} - #{anc_visit['Husband Name']} - #{anc_visit['Entity ID']}"
      key = [anc_visit['Village Code'].village.downcase, anc_visit['Wife Name'].downcase, anc_visit['Husband Name'].downcase]

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/anc_visit.json'))
      anc_visit_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      ec = get_safe_map(ecs_as_hash[key])

      anc = get_safe_map(@ancs[key])

      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "anc_visit"
      instance_id = anc_visit['Instance ID']
      entity_id = anc['Entity ID']
      submission_date = anc_visit['Submission date']

      form_instance = form_instance_erb.result(binding)
      anc_visit_json = anc_visit_erb.result(binding)
      File.open("output/ANCVisit_#{anc_visit['Entity ID']}.json", "w") do |f|
        f.puts anc_visit_json
      end
    end
  end

  def fill_hb_tests_forms
    @hb_tests.each do |hb_test|
      key_for_anc = [hb_test['Village Code'].village.downcase, hb_test['Wife Name'].downcase, hb_test['Husband Name'].downcase]
      puts "Hb Test: #{hb_test['Wife Name']} - #{hb_test['Husband Name']} - #{hb_test['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/hb_test.json'))
      hb_test_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      anc = get_safe_map(@ancs[key_for_anc])
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "hb_test"
      instance_id = hb_test['Instance ID']
      entity_id = anc['Entity ID']
      submission_date = hb_test['Submission date']

      form_instance = form_instance_erb.result(binding)
      hb_test_json = hb_test_erb.result(binding)

      File.open("output/HbTest_#{hb_test['Entity ID']}.json", "w") do |f|
        f.puts hb_test_json
      end
    end
  end

  def fill_ifa_forms
    @ifas.each do |ifa|
      key_for_anc = [ifa['Village Code'].village.downcase, ifa['Wife Name'].downcase, ifa['Husband Name'].downcase]
      puts "IFA : #{ifa['Wife Name']} - #{ifa['Husband Name']} - #{ifa['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/ifa.json'))
      ifa_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      anc = get_safe_map(@ancs[key_for_anc])

      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "ifa"
      instance_id = ifa['Instance ID']
      entity_id = anc['Entity ID']
      submission_date = ifa['Submission date']

      form_instance = form_instance_erb.result(binding)
      ifa_json = ifa_erb.result(binding)

      File.open("output/IFA_#{ifa['Entity ID']}.json", "w") do |f|
        f.puts ifa_json
      end
    end
  end

  def fill_tt_forms
    @tts.each do |tt|
      key_for_anc = [tt['Village Code'].village.downcase, tt['Wife Name'].downcase, tt['Husband Name'].downcase]
      puts "TT : #{tt['Wife Name']} - #{tt['Husband Name']} - #{tt['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/tt.json'))
      ifa_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      anc = get_safe_map(@ancs[key_for_anc])
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      dosage = tt['TT dose'].gsub('tt', '') # 1, 2 or booster
      form_name = "tt_#{dosage}"
      bind_path = "/model/instance/TT#{dosage.capitalize}_EngKan/"
      instance_id = tt['Instance ID']
      entity_id = anc['Entity ID']
      submission_date = tt['Submission date']

      form_instance = form_instance_erb.result(binding)
      ifa_json = ifa_erb.result(binding)

      File.open("output/TT#{dosage.capitalize}_#{tt['Entity ID']}.json", "w") do |f|
        f.puts ifa_json
      end
    end
  end

  def fill_delivery_outcome_forms
    @pncs.each do |pnc|
      key = [pnc['Village Code'].village.downcase, pnc['Wife Name'].downcase, pnc['Husband Name'].downcase]

      puts "Delivery Outcome registration: #{pnc['Wife Name']} - #{pnc['Husband Name']} - #{pnc['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/delivery_outcome.json'))
      out_of_area_pnc_registration_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      ec = get_safe_map(ecs_as_hash[key])
      anc = get_safe_map(@ancs[key])
      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "delivery_outcome"
      instance_id = pnc['Instance ID']
      entity_id = anc['Entity ID']
      submission_date = pnc['Submission date']

      form_instance = form_instance_erb.result(binding)
      delivery_outcome_form_json = out_of_area_pnc_registration_erb.result(binding)
      File.open("output/DO_#{pnc['Entity ID']}.json", "w") do |f|
        f.puts delivery_outcome_form_json
      end
    end
  end

  def fill_out_of_area_pnc_registration_forms
    @pncs.each do |pnc|

      puts "Out of Area PNC registration: #{pnc['Wife Name']} - #{pnc['Husband Name']} - #{pnc['Entity ID']}"

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/pnc_registration_oa.json'))
      out_of_area_pnc_registration_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "pnc_registration_oa"
      instance_id = pnc['Instance ID']
      entity_id = pnc['EC ID']
      submission_date = pnc['Submission date']

      form_instance = form_instance_erb.result(binding)
      out_of_area_pnc_registration_json = out_of_area_pnc_registration_erb.result(binding)
      File.open("output/PNCOutOfArea_#{pnc['EC ID']}.json", "w") do |f|
        f.puts out_of_area_pnc_registration_json
      end
    end
  end

  def fill_pnc_visit_forms
    @pnc_visits.each do |pnc_visit|

      puts "PNC visit: #{pnc_visit['Wife Name']} - #{pnc_visit['Husband Name']} - #{pnc_visit['Village Code'].village}"
      key = [pnc_visit['Village Code'].village.downcase, pnc_visit['Wife Name'].downcase, pnc_visit['Husband Name'].downcase]

      form_instance_erb = ERB.new(File.read('templates/form_instance_erb/pnc_visit.json'))
      pnc_visit_erb = ERB.new(File.read('templates/common_form_submission_fields.erb'))

      pnc = get_safe_map(@pncs[key])

      user_id = @mobile_worker.user_id
      user_name = @mobile_worker.user_name
      form_name = "pnc_visit"
      instance_id = pnc_visit['Instance ID']

      if pnc['OA'].downcase == "true" then
        entity_id = pnc['Entity ID']
        ecId = pnc['EC ID']
      else
        anc = get_safe_map(@ancs[key])
        ec = get_safe_map(ecs_as_hash[key])
        ecId = ec['Entity ID']
        entity_id = anc['Entity ID']
      end

      submission_date = pnc_visit['Submission date']
      visit_day = (Date.parse(pnc['Delivery date']) - Date.parse(pnc_visit['Visit date'])).to_i
      form_instance = form_instance_erb.result(binding)
      pnc_visit_json = pnc_visit_erb.result(binding)
      File.open("output/PNCVisit_#{pnc_visit['Entity ID']}.json", "w") do |f|
        f.puts pnc_visit_json
      end
    end
  end


  def has_anc?
    not (@ancs.nil? or @ancs.to_a.empty?)
  end

  def has_services?
    not (@anc_services.nil? or @anc_services.to_a.empty?)
  end

  def has_outcome?
    @anc_services.any? { |service| not service['Date of Delivery'].nil? and not service['Date of Delivery'].empty? }
  end

  def has_anc_visits?
    not (@anc_visits.nil? or @anc_visits.to_a.empty?)
  end

  def has_hb_tests?
    not (@hb_tests.nil? or @hb_tests.to_a.empty?)
  end

  def has_ifas?
    not (@ifas.nil? or @ifas.to_a.empty?)
  end

  def has_tts?
    not (@tts.nil? or @tts.to_a.empty?)
  end

  def has_pncs?
    not (@pncs.nil? or @pncs.to_a.empty?)
  end

  def has_pnc_visits?
    not (@pnc_visits.nil? or @pnc_visits.to_a.empty?)
  end

  private
  def get_safe_map(value)
    raise "Multiple values found for key : [#{value[0]['Village Code'].village}, #{value[0]['Wife Name']}, #{value[0]['Husband Name']}]" if value.size > 1
    value[0]
  end

  def ecs_as_hash
    @ec.group_by { |ec| [ec['Village Code'].village.downcase, ec['Wife Name'].downcase, ec['Husband Name'].downcase] }
  end
end

