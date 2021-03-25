/*
 * Copyright 2021 BusyMachines
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

package phms.algebra.movie

/** See [[Movie]]
  * This class is used to specify all the data needed to create a movie
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  */
//TODO: a good first candidate for extra information would be to add an image cover.
//  As an external URL, or direct upload
//  Things to consider:
//   1) if you support both cases, then maybe you need two creation case classes
//   2) image storage should be done on s3. See example of pureharm-s3 + pureharm-cloudfront in action:
//      https://github.com/busymachines/pureharm-aws/blob/master/aws-cloudfront/src/it/scala/busymachines/pureharm/aws/cloudfront/CloudfrontLiveURLSigningTest.scala
//   3.a) in case of external URL, maybe our server should download the image from the URL and store it in S3
//   3.b) in case of direct upload we should store image directly in DB
//   4) in both cases, in our DB we store a reference to S3 and return cloudfront signed URLs
//
//TODO: a more simple alternative is to just store an external URL as given,
// maybe verify that it doesn't returns a 200 OK on a GET request to it, so that we don't store gibberish.
final case class MovieCreation(
  title: MovieTitle,
  date:  Option[ReleaseDate],
)
