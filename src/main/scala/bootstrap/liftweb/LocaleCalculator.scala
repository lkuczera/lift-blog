package bootstrap.liftweb
import net.liftweb._
import common._
import java.util.Locale
import http._
import util._
import Helpers._
import _root_.net.liftweb.http.provider._
object LocaleCalculator {
  def localeCalculator(request: Box[HTTPRequest]): Locale =
    request.flatMap(r => {
      def localeCookie(in: String): HTTPCookie =
        HTTPCookie("lang", Full(in),
          Empty, Full("/"), Full(-1), Empty, Empty)
      def localeFromString(in: String): Locale = {
        val x = in.split("_").toList; new Locale(x.head, x.last)
      }
      def calcLocale: Box[Locale] =
        S.findCookie("lang").map(
          _.value.map(localeFromString)).openOr(Full(LiftRules.defaultLocaleCalculator(request)))

      S.param("locale") match {
        case Full(selectedLocale) =>
          S.addCookie(localeCookie(selectedLocale))
          tryo(localeFromString(selectedLocale))
        case _ => calcLocale
      }
    }).openOr(Locale.getDefault())
}
