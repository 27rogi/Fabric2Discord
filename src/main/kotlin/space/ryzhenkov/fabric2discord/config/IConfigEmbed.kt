package space.ryzhenkov.fabric2discord.config

interface IConfigEmbed {
    var enabled: Boolean
    var header: String
    var message: String
    var footer: String
    var color: String
    var timestamp: Boolean
    var image: String
    var iconHeader: String
    var iconFooter: String
}