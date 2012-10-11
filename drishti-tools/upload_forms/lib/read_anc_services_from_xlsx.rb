require 'roo'
require 'csv'
require 'erb'
require 'guid'
require './lib/care_import_row.rb'
require './lib/commcare.rb'
require './lib/village.rb'

class ANCServices
  def initialize xlsx_filename
    @anc_services = []

    read_from xlsx_filename
  end

  def anc_services
    @anc_services.collect {|anc_service| anc_service.to_hash}
  end

  def anc_services_per_ec
    anc_services.group_by {|service| [service['Village Code'].village, service['Wife Name'], service['Husband Name']]}
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_ANC_services.csv"

    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore
      return @anc_services unless spreadsheet.sheets.include? "ANC services"

      spreadsheet.to_csv filename, "ANC services"

      CSV.foreach(filename, { :headers => true }) do |csv_row|
        anc_service = Row.new csv_row

        anc_service.convert_value "Year", :empty => "2012"
        anc_service.convert_value "Village Name", :empty => "Village Unknown"
        anc_service.convert_value "Thayicard Number", :empty => "1234567"
        anc_service.convert_value "House number", :empty => "12345"
        anc_service.convert_value "EC Number", :empty => "111111"
        anc_service.convert_value "Old ec", :empty => "111111"
        anc_service.convert_value "Wife Name", :empty => "Wife Unknown"
        anc_service.convert_value "Wife Age", :empty => "20"
        anc_service.convert_value "Husband Name", :empty => "Husband Unknown"
        anc_service.convert_value "Husband Age", :empty => "20"
        anc_service.convert_value "Visit Number", :empty => "0"
        anc_service.convert_value "Weight", :empty => "40"
        anc_service.convert_value "BP", :empty => "120/80"
        anc_service.convert_value "HB", :empty => ""
        anc_service.convert_value "HB date", :empty => ""
        anc_service.convert_value "Albumin", :empty => ""
        anc_service.convert_to_date "Albumin test date", :empty => Date.today.to_s
        anc_service.convert_value "Sugar", :empty => ""
        anc_service.convert_to_date "Sugar test date", :empty => Date.today.to_s
        anc_service.convert_to_date "HIV test date", :empty => Date.today.to_s
        anc_service.convert_value "Micro", :empty => ""
        anc_service.convert_to_date "Micro test date", :empty => Date.today.to_s
        anc_service.convert_value "IFA tablets", :empty => ""
        anc_service.convert_to_date "IFA tablets given date", :empty => Date.today.to_s
        anc_service.convert_value "IFA tablets 2dose", :empty => ""
        anc_service.convert_to_date "IFA tablets 2dose date", :empty => Date.today.to_s
        anc_service.convert_value "IFA tablets 3dose", :empty => ""
        anc_service.convert_to_date "IFA tablets 3dose date", :empty => Date.today.to_s
        anc_service.convert_value "Blood group", :empty => ""
        anc_service.convert_value "VDRL", :empty => ""
        anc_service.convert_to_date "VDRLdate", :empty => Date.today.to_s
        anc_service.convert_value "HbS Ag", :empty => ""
        anc_service.convert_to_date "HbS Agdate", :empty => Date.today.to_s
        anc_service.convert_value "RTI/STI Detected(Male)", :empty => ""
        anc_service.convert_value "RTI/STI Detected(Female)", :empty => ""
        anc_service.convert_value "RTI/STI Treatement(Male)", :empty => ""
        anc_service.convert_value "RTI/STI Treatement(Female)", :empty => ""
        anc_service.convert_value "Pragnancy Outcome", :empty => ""
        anc_service.convert_to_date "Outcome date", :empty => Date.today.to_s
        anc_service.convert_value "Reason for High risk", :empty => ""
        anc_service.convert_value "Left the place", :empty => ""
        anc_service.convert_to_date "Date of Left the Place", :empty => Date.today.to_s
        anc_service.convert_value "e.Year", :empty => "2012"
        anc_service.convert_value "e.Wife Name", :empty => "Wife Unknown"
        anc_service.convert_value "e.Wife Age", :empty => "20"
        anc_service.convert_value "e.Husband Name", :empty => "Husband Unknown"
        anc_service.convert_value "Sno", :empty => "1"
        anc_service.convert_to_date "Registration date", :empty => Date.today.to_s
        anc_service.convert_value "aHouse Number", :empty => "12345"
        anc_service.convert_value "New EC No", :empty => "111111"
        anc_service.convert_value "Old  EC No", :empty => "111111"
        anc_service.convert_value "Thayi Card Number", :empty => "1234567"
        anc_service.convert_value "e.Husband Age", :empty => "20"
        anc_service.convert_value "Address", :empty => "Unknown"
        anc_service.convert_value "Religion", :default => "r_others"
        anc_service.convert_value "Education", :empty => ""
        anc_service.convert_value "Occupation", :empty => ""
        anc_service.convert_value "No of Living Male Children", :empty => "0"
        anc_service.convert_value "No of Living Female Children", :empty => "0"
        anc_service.convert_value "Gravida", :empty => "0"
        anc_service.convert_value "Number of parity(P)", :empty => "0"
        anc_service.convert_value "Number of livebirth(L)", :empty => "0"
        anc_service.convert_value "Number of abortion(A)", :empty => "0"
        anc_service.convert_value "Duration of Pregnancy in Weeks", :empty => "0"
        anc_service.convert_to_date "LMP", :empty => Date.today.to_s
        anc_service.convert_to_date "ANC Checkup", :empty => anc_service['LMP']
        anc_service.convert_to_date "EDD", :empty => Date.today.to_s
        anc_service.convert_value "Height", :empty => "150"
        anc_service.convert_value "No of ANM Visits", :empty => "0"
        anc_service.convert_to_date "Date of Delivery", :empty => ""
        anc_service.convert_value "Child Weight at Delivery time", :empty => ""
        anc_service.convert_value "Place of Delivery", :empty => ""
        anc_service.convert_to_date "e.Date of Left the Place", :empty => Date.today.to_s
        anc_service.convert_to_date "eRegistration date", :empty => Date.today.to_s
        anc_service.convert_value "eHouse Number", :empty => "12345"
        anc_service.convert_value "e.EC Number", :empty => "111111"
        anc_service.convert_value "eWife Name", :empty => "Wife Unknown"
        anc_service.convert_value "eWife Age", :empty => "20"
        anc_service.convert_value "eHusband Name", :empty => "Husband Unknown"
        anc_service.convert_value "Number of Abortion", :empty => "0"
        anc_service.convert_value "Number of Still Birth", :empty => "0"
        anc_service.convert_value "Number of Living Children", :empty => "0"
        anc_service.convert_to_date "DOB of youngest child", :empty => Date.today.to_s
        anc_service.convert_to_date "Acceptance Date", :empty => Date.today.to_s
        anc_service.convert_to_date "if Yes LMP date", :empty => Date.today.to_s
        anc_service.convert_value "eThayi Card Number", :empty => "1234567"

        anc_service.convert_value "Village Code",
          "29230030060" => Village.new("bherya", "munjanahalli", "munjanahalli"),
          "29230030061" => Village.new("bherya", "munjanahalli", "chikkabheriya"),
          "29230030058" => Village.new("bherya", "munjanahalli", "kaval_hosur"),
          :empty        => Village.new("bherya", "munjanahalli", "munjanahalli"),
          :default      => Village.new("bherya", "munjanahalli", "munjanahalli")

        anc_service.convert_value "e.Village Code",
          "29230030060" => Village.new("bherya", "munjanahalli", "munjanahalli"),
          "29230030061" => Village.new("bherya", "munjanahalli", "chikkabheriya"),
          "29230030058" => Village.new("bherya", "munjanahalli", "kaval_hosur"),
          :empty        => Village.new("bherya", "munjanahalli", "munjanahalli"),
          :default      => Village.new("bherya", "munjanahalli", "munjanahalli")

        anc_service.convert_value "eVillage Code",
          "29230030060" => Village.new("bherya", "munjanahalli", "munjanahalli"),
          "29230030061" => Village.new("bherya", "munjanahalli", "chikkabheriya"),
          "29230030058" => Village.new("bherya", "munjanahalli", "kaval_hosur"),
          :empty        => Village.new("bherya", "munjanahalli", "munjanahalli"),
          :default      => Village.new("bherya", "munjanahalli", "munjanahalli")

        anc_service.convert_value "Caste",
          "SC"     => "sc",
          "ST"     => "st",
          :empty   => "c_others",
          :default => "c_others"

        anc_service.convert_value "HRP",
          "No"     => "no",
          "Yes"    => "yes",
          :empty   => "no",
          :default => "no"

        anc_service.convert_value "BPL",
          "Yes"    => "bpl",
          "No"     => "apl",
          :empty   => "apl",
          :default => "apl"

        anc_service.convert_value "Out of area",
          "No"     => "no",
          "Yes"    => "yes",
          :empty   => "no",
          :default => "no"

        anc_service.convert_value "FP Method",
          "OP"             => "ocp",
          "IUD"            => "iud",
          "Condom"         => "condom",
          "Sterilization"  => "female_sterilization",
          :empty           => "none",
          :default         => "none"

        anc_service.convert_value "TT1",
          "B/D"     => "",
          :empty    => ""

        anc_service.convert_value "TT2",
          "B/D"     => "",
          :empty    => ""

        anc_service.convert_value "HIV",
          "Positive" => "HIV",
          :default   => "",
          :empty     => ""

        anc_service.convert_value "Outcomes",
          "Live birth"  => "live_birth",
          "Still birth" => "still_birth",
          :empty => "live_birth"

        anc_service.add_field "Instance ID", Guid.new.to_s
        anc_service.add_field "Outcome Instance ID", Guid.new.to_s
        anc_service.add_field "BP Systolic", anc_service['BP'].gsub(/\/.*/, '')
        anc_service.add_field "BP Diastolic", anc_service['BP'].gsub(/^.*\//, '')

        anc_service.add_field "TT Shot Given?", (not anc_service['TT1'].empty? or not anc_service['TT2'].empty?) ? "yes" : "no"

        tt_dose_given = ""
        tt_dose_given = "tt1" unless anc_service['TT1'].empty?
        tt_dose_given = "tt2" unless anc_service['TT2'].empty?

        anc_service.add_field "TT Dose", tt_dose_given

        @anc_services << anc_service
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
