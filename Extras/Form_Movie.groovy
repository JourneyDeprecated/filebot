{ import java.math.RoundingMode
  import net.filebot.Language
  def norm = { it.upperInitial()
             .lowerTrail()
             .replaceTrailingBrackets()
             .replaceAll(/[`´‘’ʻ""“”]/, "'")
             .replaceAll(/[:|]/, " - ")
             .replaceAll(/[?]/, "!")
             .replaceAll(/[*\s]+/, " ")
             .replaceAll(/\b[IiVvXx]+\b/, { it.upper() })
             .replaceAll(/\b[0-9](?i:th|nd|rd)\b/, { it.lower() }) }
 allOf
  // Movies directory
  {"Movies"}
  // Folder name
  {n.colon(" - ") + " [$y]"}
  { allOf
    // File name
    { primaryTitle ? primaryTitle.colon(" - ") : primaryTitle.colon(" - ") }
    {" [$y]"}
    // tags + a few more variants
    { specials = { allOf
                     {tags}
                     { def last = n.tokenize(" ").last()
                       fn.after(/(?i:$last)/).findAll(/(?i:alternate[ ._-]cut|limited)/)*.upperInitial()*.lowerTrail()*.replaceAll(/[._-]/, " ") }
                     .flatten().sort() }
      specials().size() > 0 ? specials().join(", ").replaceAll(/^/, " - ") : "" }
    {" PT $pi"}
    {" ["}
    { allOf
      // Video stream
      { allOf{vf}{vc}.join(" ") }
      { def audioClean = { it.replaceAll(/[\p{Pd}\p{Space}]/, ' ').replaceAll(/\p{Space}{2,}/, ' ') }
        // map Codec + Format Profile
        def mCFP = [ "AC3" : "AC3",
                     "AC3+" : "E-AC3",
                     "AAC LC LC" : "AAC-LC",
                     "AAC LC SBR HE AAC LC": "HE-AAC" ]
        audio.collect { au ->
        def channels = any{ au['ChannelPositions/String2'] }{ au['Channel(s)_Original'] }{ au['Channel(s)'] }
        def ch = channels.replaceAll(/Object\sBased\s\/|0.(?=\d.\d)/, '')
                         .tokenize('\\/').take(3)*.toDouble()
                         .inject(0, { a, b -> a + b }).findAll { it > 0 }
                         .max().toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
        def codec = audioClean(any{ au['CodecID/String'] }{ au['Codec/String'] }{ au['Codec'] })
        def format = any{ au['CodecID/Hint'] }{ au['Format'] }
        def format_profile = { if ( au['Format_Profile'] != null) audioClean(au['Format_Profile']) else '' }
        def combined = allOf{codec}{format_profile}.join(' ')
        def stream = allOf
                       { ch }
                       { mCFP.get(combined, format) }
                       { Language.findLanguage(au['Language']).ISO3.upperInitial() }
        return stream }*.join(" ").join(", ") }
        // logo-free release source finder
      { def websources = readLines("C:/Users/JourneyOver/Dropbox/Public/Folders/Filebot/websources.txt").join("|")
        def lfr = { fn.match(/($websources)\.(?i)WEB/) }
        return allOf{lfr}{"${self.source ?: 'WEB-DL'}"}.join(" ") }
      .join(" - ") }
    {"]"}
    {subt}
    .join("") }
  .join("/") }
