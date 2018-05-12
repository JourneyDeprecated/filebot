// TVDB/TMDB ID
def id = id as int

// Video Type
def type = type

// Sonarr API Configuration
if (type.equals('Episode')) {
    def url = new URL('http://localhost:8989')
    def header = ['X-Api-Key': 'PLACEAPIKEYHERE']

    def sonarrSeriesId = new JsonSlurper()
        .parseText(new URL(url, '/api/series')
            .get(header)
            .text)
        .find {
            it.tvdbId == id
        }.id

    println new URL(url, '/api/command').post(
        JsonOutput.toJson(
            [name: 'rescanSeries', seriesId: sonarrSeriesId]
        ).getBytes('UTF-8'),
        'application/json',
        header
    ).text
}

// Radarr API Configuration
if (type.equals('Movie')) {
    def url = new URL('http://localhost:7878')
    def header = ['X-Api-Key': 'PLACEAPIKEYHERE']

    def radarrMovieId = new JsonSlurper()
        .parseText(new URL(url, '/api/movie')
            .get(header)
            .text)
        .find {
            it.tmdbId == id
        }.id

    println new URL(url, '/api/command').post(
        JsonOutput.toJson(
            [name: 'rescanMovie', movieId: radarrMovieId]
        ).getBytes('UTF-8'),
        'application/json',
        header
    ).text
}
