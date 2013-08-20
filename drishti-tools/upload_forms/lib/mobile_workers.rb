class MobileWorker
  attr_reader :user_name, :spreadsheet, :user_id

  def initialize user_name, spreadsheet
    @spreadsheet = spreadsheet
    @user_name = user_name
    @user_id = user_name
  end
end

class MobileWorkers

  def initialize
    @mobileWorkers = {
        "c" => MobileWorker.new("c", "examples/Munjanhalli.xlsx"),
        "d" => MobileWorker.new("d", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
        "e" => MobileWorker.new("e", "examples/Keelanapura.xlsx"),
        "test_upload" => MobileWorker.new("test_upload", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
        "bhe1" => MobileWorker.new("bhe1", "examples/1 ElizabethD'Souza Bheriya-A.xlsx"),
        "bhe2" => MobileWorker.new("bhe2", "examples/2 Jyothilakshmi Bheriya-B.xlsx"),
        "bhe3" => MobileWorker.new("bhe3", "examples/3 Fathima GAGuppe.xlsx"),
        "bhe4" => MobileWorker.new("bhe4", "examples/4 Hemalatha Hosaagrahara.xlsx"),
        "bhe5" => MobileWorker.new("bhe5", "examples/5 Latha Munjanhalli.xlsx"),
        "klp1" => MobileWorker.new("klp1", "examples/8 Dhakshakanye PGHundi.xlsx"),
        "klp2" => MobileWorker.new("klp2", "examples/7 N.R Kamalakshi Meghalapura.xlsx"),
        "klp3" => MobileWorker.new("klp3", "examples/9 B.T Indira Vajamangala.xlsx"),
        "klp4" => MobileWorker.new("klp4", "examples/6 Hemavathi Keelanapura.xlsx"),
        "demo1" => MobileWorker.new("demo1", "examples/10 Training data.xlsx"),
        "demo2" => MobileWorker.new("demo2", "examples/10 Training data.xlsx"),
        "trainer1" => MobileWorker.new("trainer1", "examples/10 Training data.xlsx"),
        "trainer2" => MobileWorker.new("trainer2", "examples/10 Training data.xlsx"),
        "user1" => MobileWorker.new("user1", "examples/10 Training data.xlsx"),
        "user2" => MobileWorker.new("user2", "examples/10 Training data.xlsx"),
        "user3" => MobileWorker.new("user3", "examples/10 Training data.xlsx"),
        "user4" => MobileWorker.new("user4", "examples/10 Training data.xlsx"),
        "user5" => MobileWorker.new("user5", "examples/10 Training data.xlsx"),
        "user6" => MobileWorker.new("user6", "examples/10 Training data.xlsx"),
        "user7" => MobileWorker.new("user7", "examples/10 Training data.xlsx"),
        "user8" => MobileWorker.new("user8", "examples/10 Training data.xlsx"),
        "user9" => MobileWorker.new("user9", "examples/10 Training data.xlsx"),
        "user10" => MobileWorker.new("user10", "examples/10 Training data.xlsx")
    }
  end

  def find_by_user_name user_name
    @mobileWorkers[user_name]
  end

end
