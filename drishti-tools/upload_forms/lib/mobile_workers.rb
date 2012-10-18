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
        "d" => MobileWorker.new("d", "dUserID", "examples/Bheriya.xlsx"),
        "e" => MobileWorker.new("e", "eUserID", "examples/Keelanapura.xlsx"),
        "test_upload" => MobileWorker.new("test_upload", "1def74f43eb5296a26341b6ac24b44c9", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
        "elizab" => MobileWorker.new("elizab", "elizabUserID", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
        "jyothi" => MobileWorker.new("jyothi", "jyothiUserID", "examples/2 Jyothilakshmi Bheriya-B.xlsx"),
        "fathim" => MobileWorker.new("fathim", "fathimUserID", "examples/3 Fathima GAGuppe.xlsx"),
        "hemala" => MobileWorker.new("hemala", "hemalaUserID", "examples/4 Hemalatha Hosaagrahara.xlsx"),
        "latha" => MobileWorker.new("latha", "lathaUserID", "examples/5 Latha Munjanhalli.xlsx"),
        "hemava" => MobileWorker.new("hemava", "hemavaUserID", "examples/6 Hemavathi Keelanapura.xlsx"),
        "kamala" => MobileWorker.new("kamala", "kamalaUserID", "examples/7 N.R Kamalakshi Meghalapura.xlsx"),
        "dhaksh" => MobileWorker.new("dhaksh", "dhakshUserID", "examples/8 Dhakshakanye PGHundi.xlsx"),
        "indira" => MobileWorker.new("indira", "indiraUserID", "examples/9 B.T Indira Vajamangala.xlsx")
    }
  end

  def find_by_user_name user_name
    @mobileWorkers[user_name]
  end

end
