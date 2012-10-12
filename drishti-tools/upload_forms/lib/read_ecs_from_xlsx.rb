require 'roo'
require 'csv'
require 'erb'
require 'guid'
require './lib/care_import_row.rb'
require './lib/commcare.rb'
require './lib/village.rb'

class ECs
  def initialize xlsx_filename
    @ecs = []

    read_from xlsx_filename
  end

  def ecs
    @ecs.collect {|ec| ec.to_hash}
  end

  private
  def read_from xlsx_filename
    filename = "#{Random.rand(9999999)}_EC_register.csv"

    begin
      spreadsheet = Excelx.new xlsx_filename, nil, :ignore
      spreadsheet.to_csv filename, "EC register"

      CSV.foreach(filename, { :headers => true }) do |csv_row|
        ec = Row.new csv_row

        ec.convert_to_date "Registration date", :empty => Date.today.to_s
        ec.convert_value "House Number", :empty => "111111"
        ec.convert_value "EC Number", :empty => "111111"
        ec.convert_value "Wife Name", :empty => "Wife Unknown"
        ec.convert_value "Wife Age", :empty => "20"
        ec.convert_value "Husband Name", :empty => "Husband Unknown"
        ec.convert_value "Number of Abortion", :empty => "0"
        ec.convert_value "Number of Still Birth", :empty => "0"
        ec.convert_value "Number of Living Children", :empty => "0"
        ec.convert_to_date "DOB of youngest child", :empty => "0"
        ec.convert_value "Status of EC Woman [Permanent/ Continuting/Discontinued]", :empty => "Continuting"
        ec.convert_to_date "Acceptance Date", :empty => Date.today.to_s
        ec.convert_value "Pregnancy [Yes/No]", :empty => "No"
        ec.convert_value "if Yes LMP date", :empty => ""
        ec.convert_value "Thayi Card Number", :empty => "1234567"

        ec.convert_value "Village Code",
          "29230030066" => Village.new("bheriya","bheriya-a","ardha bheriya"),
          "29230060072" => Village.new("bheriya","bheriya-a","basavanapura"),
          "29230030063" => Village.new("bheriya","bheriya-a","gerdada"),
          "29230030067" => Village.new("bheriya","bheriya-a","sambaravalli"),
          "29230030064" => Village.new("bheriya","bheriya-a","somanahalli colony"),
          "29230030065" => Village.new("bheriya","bheriya-b","battiganahalli"),
          "29230030069" => Village.new("bheriya","bheriya-b","sugganahalli"),
          "29230030059" => Village.new("bheriya","guluvinahattiguppe","arakere"),
          "29230030056" => Village.new("bheriya","guluvinahattiguppe","g.a.guppe"),

          "29230030071" => Village.new("bheriya","hosaagrahara","harambanahalli"),
          "29230030070" => Village.new("bheriya","hosaagrahara","hosaagrahara"),
          "29230030068" => Village.new("bheriya","hosaagrahara","mandiganahalli"),
          "29230030061" => Village.new("bheriya","munjanahalli","chikkabheriya"),
          "29230030058" => Village.new("bheriya","munjanahalli","kaval_hosur"),
          "29230030060" => Village.new("bheriy","munjanahalli","munjanahalli"),
          "29230040138" => Village.new("keelanapura","keelanapura","inamuttanahalli"),
          "29230040113" => Village.new("keelanapura","keelanapura","keelanapura"),

          "29230040137" => Village.new("keelanapura","megalapura","hosahalli"),
          "29230040112" => Village.new("keelanapura","megalapura","madavagere"),
          "29230040114" => Village.new("keelanapura","megalapura","megalapura"),
          "29230040111" => Village.new("keelanapura","puttegowdanahundi","chatnahallipalya"),
          "29230040116" => Village.new("keelanapura","puttegowdanahundi","duddagere"),

          "29230040110" => Village.new("keelanapura","puttegowdanahundi","puttegowdanahundi"),
          "29230040104" => Village.new("keelanapura","vajamangala","chikkahalli"),
          "29230040101" => Village.new("keelanapura","vajamangala","vajamangala"),

          :empty        => Village.new("bherya", "munjanahalli", "munjanahalli"),
          :default      => Village.new("bherya", "munjanahalli", "munjanahalli")


        ec.convert_value "FP Method",
          "OP"             => "ocp",
          "IUD"            => "iud",
          "Condom"         => "condom",
          "Sterilization"  => "female_sterilization",
          :empty           => "none",
          :default         => "none"

        ec.add_field "Case ID", Guid.new.to_s
        ec.add_field "Instance ID", Guid.new.to_s

        ec.add_field "Number of Pregnancies", (ec['Number of Abortion'].to_i + ec['Number of Still Birth'].to_i + ec['Number of Living Children'].to_i).to_s
        ec.add_field "FP Start Date", (Date.parse(ec['Registration date']).to_s rescue Date.today.to_s)

        @ecs << ec
      end
    ensure
      FileUtils.rm_f filename
    end
  end
end
