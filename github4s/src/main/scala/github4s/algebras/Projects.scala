/*
 * Copyright 2016-2021 47 Degrees Open Source <https://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github4s.algebras

import github4s.GHResponse
import github4s.domain._

trait Projects[F[_]] {

  /**
   * List the projects belonging to a specific organization
   *
   * @param org Organization for which we want to retrieve the projects
   * @param state Filter projects returned by their state. Can be either `open`, `closed`, `all`.
   *              Default: `open`
   * @param pagination Limit and Offset for pagination
   * @param headers Optional user headers to include in the request
   * @return GHResponse with the list of projects belonging to this organization
   */
  def listProjects(
      org: String,
      state: Option[String] = None,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Project]]]

  /**
   * List the projects belonging to a specific repository
   *
   * @param owner of the repo
   * @param repo name of the repo
   * @param state Filter projects returned by their state. Can be either `open`, `closed`, `all`.
   *              Default: `open`
   * @param pagination Limit and Offset for pagination
   * @param headers Optional user headers to include in the request
   * @return GHResponse with lists the projects in a repository.
   */
  def listProjectsRepository(
      owner: String,
      repo: String,
      state: Option[String] = None,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Project]]]

  /**
   * List the columns belonging to a specific project id
   *
   * @param project_id Project id for which we want to retrieve the columns
   * @param pagination Limit and Offset for pagination
   * @param headers Optional user headers to include in the request
   * @return GHResponse with the list of columns belonging to this project id
   */
  def listColumns(
      project_id: Long,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Column]]]

  /**
   * List the cards belonging to a specific column id
   *
   * @param column_id Column id for which we want to retrieve the cards
   * @param archived_state Filters the project cards that are returned by the card's state.
   *                       Can be one of all,archived, or not_archived. Default: not_archived
   * @param pagination Limit and Offset for pagination
   * @param headers Optional user headers to include in the request
   * @return GHResponse with the list of cards belonging to this column id
   */
  def listCards(
      column_id: Long,
      archived_state: Option[String] = None,
      pagination: Option[Pagination] = None,
      headers: Map[String, String] = Map()
  ): F[GHResponse[List[Card]]]

}
