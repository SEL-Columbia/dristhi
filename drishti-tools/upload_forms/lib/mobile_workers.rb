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
        "d" => MobileWorker.new("d", "c3d86928a9c501cd88f42d6031ad70c9", "examples/Bheriya.xlsx"),
        "e" => MobileWorker.new("e", "c3d86928a9c501cd88f42d6031ad70c9", "examples/Keelanapura.xlsx")
    }
  end

  def find_by_user_name user_name
    @mobileWorkers[user_name]
  end

end