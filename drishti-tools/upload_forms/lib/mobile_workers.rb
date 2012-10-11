class MobileWorker
  attr_reader :user_id, :spreadsheet

  def initialize user_id, spreadsheet
    @user_id = user_id
    @spreadsheet = spreadsheet
  end
end

class MobileWorkers

  def initialize
    @mobileWorkers = {
        "c" => MobileWorker.new("c3d86928a9c501cd88f42d6031ad70c9", "examples/Munjanhalli.xlsx"),
        "d" => MobileWorker.new("c3d86928a9c501cd88f42d6031ad70c9", "examples/Bheriya.xlsx"),
        "e" => MobileWorker.new("c3d86928a9c501cd88f42d6031ad70c9", "examples/Keelanapura.xlsx")
    }
  end

  def spreadsheet_for user_name
    @mobileWorkers[user_name].spreadsheet
  end

  def user_id_for user_name
    @mobileWorkers[user_name].spreadsheet
  end

end