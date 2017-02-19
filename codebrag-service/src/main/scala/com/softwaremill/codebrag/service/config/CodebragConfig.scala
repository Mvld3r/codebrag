package com.softwaremill.codebrag.service.config

import java.util.Map.Entry

import com.softwaremill.codebrag.common.config.ConfigWithDefault
import com.typesafe.config.ConfigValue
import com.typesafe.scalalogging.slf4j.Logging
import org.joda.time._

import scala.collection.JavaConversions._
import scala.concurrent.duration._

trait CodebragConfig extends ConfigWithDefault with StatsConfig with EmailNotificationConfig {

  lazy val demo: Boolean = getBoolean("codebrag.demo", default = false)
  lazy val applicationUrl: String = getString("codebrag.application-url", "http://localhost:8080")
  lazy val title: String = getString("codebrag.title", "Codebrag")

  lazy val invitationExpiryTime: ReadablePeriod = Period.millis(getMilliseconds("codebrag.invitation-expiry-time", 24.hours.toMillis).toInt)

  lazy val pullSleepPeriodEnabled = getBoolean("codebrag.pull-sleep-period.enabled", default = false)
  lazy val pullSleepPeriodStart = getInt("codebrag.pull-sleep-period.from", 22)
  lazy val pullSleepPeriodEnd = getInt("codebrag.pull-sleep-period.to", 5)
}

trait EmailNotificationConfig extends ConfigWithDefault {
  lazy val userNotifications: Boolean = getBoolean("email-notifications.enabled", default = true)
  lazy val notificationsCheckInterval = getMilliseconds("email-notifications.check-interval", 15.minutes.toMillis).millis
  lazy val userOfflinePeriod = Period.millis(getMilliseconds("email-notifications.user-offline-after", 5.minutes.toMillis).toInt)

  // Config properties for daily summary send outs
  // Not included in docs and in config template as we prefer not to expose them
  // By default daily summaries are sent at 6am every day
  lazy val dailyDigestSendHour = getInt("email-notifications.daily-digest-hour", 6)
  lazy val dailyDigestSendMinute = getInt("email-notifications.daily-digest-minute", 0)
  lazy val dailyDigestSendInterval = Period.millis(getMilliseconds("email-notifications.daily-digest-interval", 24.hours.toMillis).toInt)
}

trait StatsConfig extends ConfigWithDefault {
  lazy val appVersion = getString("codebrag.version", "2.3")
  lazy val sendStats = getBoolean("codebrag.send-anon-usage-data", false)
  lazy val statsSendHour = getInt("codebrag.stats-send-hour", 3)
  lazy val statsSendMinute = getInt("codebrag.stats-send-minute", 0)
  lazy val statsSendInterval = getMilliseconds("codebrag.stats-send-interval", 24.hours.toMillis).millis
  lazy val dailyStatsServerUrl = getString("codebrag.daily-stats-server-url", "https://stats.codebrag.com:6666")
  lazy val instanceRunStatsServerUrl = getString("codebrag.instance-run-stats-server-url", "https://stats.codebrag.com:6666/instanceRun")
}

trait HooksConfig extends ConfigWithDefault with Logging {

  /**
   * Holds Map of listeners where key is name of the hook
   * @see com.softwaremill.codebrag.Hookable#hookName
   */
  lazy val eventHooks: Map[String, List[String]] = {
    if (rootConfig.hasPath("hooks")) {
      logger.debug("'hooks' section was defined in the config file!")

      rootConfig.getConfig("hooks").entrySet().toSet.map { entry: Entry[String, ConfigValue] =>
        (entry.getKey, entry.getValue.atKey(entry.getKey).getStringList(entry.getKey).toList)
      }.toMap

    } else {
      logger.debug("'hooks' config not defined, WebHooks won't be propagated!")
      Map()
    }
  }

}
