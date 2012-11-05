class MobileWorker
  attr_reader :user_name, :user_id, :spreadsheet

  def initialize user_name, user_id, spreadsheet
    @user_id = user_id
    @spreadsheet = spreadsheet
    @user_name = user_name
  end
end

class MobileWorkers

  def initialize
    @mobileWorkers = {
        "c" => MobileWorker.new("c", "1431c5123e2e72ac8183dd6a987960ab", "examples/Munjanhalli.xlsx"),
        "d" => MobileWorker.new("d", "1def74f43eb5296a26341b6ac2cb38c8", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
        "e" => MobileWorker.new("e", "eUserID", "examples/Keelanapura.xlsx"),
        "test_upload" => MobileWorker.new("test_upload", "1def74f43eb5296a26341b6ac24b44c9", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
        "bhe1" => MobileWorker.new("bhe1", "1def74f43eb5296a26341b6ac29a417c", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
        "bhe2" => MobileWorker.new("bhe2", "1def74f43eb5296a26341b6ac29a360a", "examples/2 Jyothilakshmi Bheriya-B.xlsx"),
        "bhe3" => MobileWorker.new("bhe3", "1def74f43eb5296a26341b6ac29a356f", "examples/3 Fathima GAGuppe.xlsx"),
        "bhe4" => MobileWorker.new("bhe4", "3bcf3a920c20410da9eea4dcec922e16", "examples/4 Hemalatha Hosaagrahara.xlsx"),
        "bhe5" => MobileWorker.new("bhe5", "1def74f43eb5296a26341b6ac29a1fde", "examples/5 Latha Munjanhalli.xlsx"),
        "klp1" => MobileWorker.new("klp1", "1def74f43eb5296a26341b6ac29a1202", "examples/8 Dhakshakanye PGHundi.xlsx"),
        "klp2" => MobileWorker.new("klp2", "1def74f43eb5296a26341b6ac29a0646", "examples/7 N.R Kamalakshi Meghalapura.xlsx"),
        "klp3" => MobileWorker.new("klp3", "1def74f43eb5296a26341b6ac299f79e", "examples/9 B.T Indira Vajamangala.xlsx"),
        "demo" => MobileWorker.new("demo", "102319141fe0fabde16f4222b615b9bf", "examples/5 Latha Munjanhalli.xlsx")
    }
  end

  def find_by_user_name user_name
    @mobileWorkers[user_name]
  end

end
