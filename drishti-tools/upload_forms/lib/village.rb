class Village

  def self.code_to_village_hash
    {
        "29230030066" => Village.new("bherya", "bherya-a", "ardha_bherya"),
        "29230060072" => Village.new("bherya", "bherya-a", "basavanapura"),
        "29230030063" => Village.new("bherya", "bherya-a", "geradada"),
        "29230030067" => Village.new("bherya", "bherya-a", "sambaravalli"),
        "29230030064" => Village.new("bherya", "bherya-a", "somanahalli colony"),
        "29230030065" => Village.new("bherya", "bherya-b", "battiganahalli"),
        "29230030069" => Village.new("bherya", "bherya-b", "sugganahalli"),
        "29230030059" => Village.new("bherya", "g.a.guppe", "arakere"),
        "29230030056" => Village.new("bherya", "g.a.guppe", "g.a.guppe"),
        "29230030071" => Village.new("bherya", "hosa_agrahara", "harambanahalli"),
        "29230030070" => Village.new("bherya", "hosa_agrahara", "hosa_agrahara"),
        "29230030068" => Village.new("bherya", "hosa_agrahara", "mandiganahalli"),
        "29230030061" => Village.new("bherya", "munjanahalli", "chikkabherya"),
        "29230030058" => Village.new("bherya", "munjanahalli", "kavalu_hosur"),
        "29230030060" => Village.new("bheriy", "munjanahalli", "munjanahalli"),
        "29230040138" => Village.new("keelanapura", "keelanapura-a", "inam_uttanahalli"),
        "29230040113" => Village.new("keelanapura", "keelanapura-a", "keelanapura"),
        "29230040137" => Village.new("keelanapura", "keelanapura-b", "hosahalli"),
        "29230040112" => Village.new("keelanapura", "keelanapura-b", "madhavagere"),
        "29230040114" => Village.new("keelanapura", "keelanapura-b", "megalapura"),
        "29230040111" => Village.new("keelanapura", "puttegowdanahundi", "chattanahallipalya"),
        "29230040116" => Village.new("keelanapura", "puttegowdanahundi", "duddagere"),
        "29230040039" => Village.new("keelanapura", "puttegowdanahundi", "lakshmipura"),
        "29230040110" => Village.new("keelanapura", "puttegowdanahundi", "puttegowdanahundi"),
        "29230040104" => Village.new("keelanapura", "vajamangala", "chikkahalli"),
        "29230040101" => Village.new("keelanapura", "vajamangala", "vajamangala"),
        :empty => Village.new("bherya", "munjanahalli", "munjanahalli"),
        :default => Village.new("bherya", "munjanahalli", "munjanahalli")
    }
  end


  attr_reader :phc, :sc, :village

  def initialize phc, sc, village
    @phc = phc
    @sc = sc
    @village = village
  end

end