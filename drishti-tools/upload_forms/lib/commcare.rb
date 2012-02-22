# You need to do a: gem install multipart-post
require 'net/http/post/multipart'

class CommCare
  def upload content
    file_contents = StringIO.new content
    url = URI.parse('https://www.commcarehq.org/a/frhs-who-columbia/receiver/')
    req = Net::HTTP::Post::Multipart.new url.path, "xml_submission_file" => UploadIO.new(file_contents, "application/xml", "file.xml")
    http = Net::HTTP.new(url.host, url.port)
    http.use_ssl = true

    http.start { |http| http.request(req) }
  end
end

