package io

import play.api.libs.json.{Json, OFormat}

/**
 * Pet is an animal who is considered to be a family member
 *
 * @param name Name of a dashboard*
 * @param description Description
 * @param parentId if has any parent.
 */
case class Dashboard(
                 name: String,
                 description: String,
                parentId: String,
              )

object Dashboard {

  implicit val petFormat: OFormat[Dashboard] = Json.format[Dashboard]

}
