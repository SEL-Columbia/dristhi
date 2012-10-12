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
        "c" => MobileWorker.new("c", "c3d86928a9c501cd88f42d6031ad70c9", "examples/Munjanhalli.xlsx"),
        "d" => MobileWorker.new("d", "dUserID", "examples/Bheriya.xlsx"),
        "e" => MobileWorker.new("e", "eUserID", "examples/Keelanapura.xlsx"),
        "test_upload" => MobileWorker.new("test_upload", "cd0435c0a929ceec9adb9dfb61bd01b7", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
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