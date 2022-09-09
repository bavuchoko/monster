# 게시판 기본 CRUD

        ### spring boot, rest-api, jpa, security-jwt 연습을 위한 Repositoy

        ### 게시글 조회
        #### REQUEST
        ##### GET 요청을 사용하여 서비스의 모든 게시글을 조회할 수 있다.


        ##### -Curl request
        $ curl 'https://pjs.or.kr:8080/api/content/JAVA?page=1&size=8&sort=writeTime%2CDESC' -i -X GET
        -HTTP request
        GET /api/content/JAVA?page=1&size=8&sort=writeTime%2CDESC HTTP/1.1
        Host: pjs.or.kr:8080



        #### RESPONSE

        ##### -HTTP response example [응답예제]
        {
        "id" : 74,
        "category" : "java",
        "title" : "제목",
        "body" : "내용",
        "bodyHtml" : "내용",
        "thumbnail" : null,
        "writeTime" : "2022-08-05 14:30:00",
        "updateTime" : null,
        "hitCout" : 0,
        "account" : {
        "id" : 73,
        "nickname" : "nick"
        },
        "replies" : null,
        "_links" : {
        "query-content" : {
        "href" : "http://localhost:8080/api/content/java"
        },
        "self" : {
        "href" : "http://localhost:8080/api/content/java/74"
        },
        "update-content" : {
        "href" : "http://localhost:8080/api/content/java/74"
        },
        "profile" : {
        "href" : "/docs/asciidoc/api.html#resources-content-create"
        }
        }
        }


        ##### -HTTP response fields
        | Path | Type | Description |
        |:---|:---|:---|
        | id | Number | 게시글의 식별자 |
        | title | String | 게시글의 식별자 |
        | body | String | 본문 마크업 |
        | bodyHtml | String | 이지웍 형태의 본문 150자 > 미리보기 |
        | writeTime | String | 작성일자 |
        | updateTime | Null | 수정일자 |
        | _links.self.href | String | 셀프 링크 |
        | _links.query-content.href | String | 조회 링크 |
        | _links.update-content.href | String | 수정 링크 |
        | _links.profile.href | String | 프로필자 |



        ### 게시글 생성
        #### REQUEST
        ##### POST 요청을 사용해서 새 게시글을 만들 수 있다.

        ##### -Curl request
        $ curl 'https://pjs.or.kr:8080/api/content/java' -i -X POST \
        -H 'Content-Type: application/json;charset=UTF-8' \
        -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJuaWNrbmFtZSI6Im5pY2siLCJ1c2VybmFtZSI6InVzZXIyQG1haWwuY29tIiwiaWF0IjoxNjYyNjA0OTE1LCJleHAiOjE2NjI2OTEzMTV9.UV1uT25YfsEdOpRBZ6oiIFxLIZ_YYbVBpG31_Fvl9u5PIgjncap-aEGunfbRdPKB2NDBk_JVccYWYQUWNvg5BQ' \
        -H 'Accept: application/hal+json' \
        -d '{
        "category" : "java",
        "thumbnail" : null,
        "title" : "제목",
        "body" : "내용",
        "bodyHtml" : "내용",
        "writeTime" : "2022-08-05T14:30:00",
        "updateTime" : null,
        "hitCout" : 0,
        "replies" : null
        }'



        ##### -HTTP request
        POST /api/content/java HTTP/1.1
        Content-Type: application/json;charset=UTF-8
        Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJuaWNrbmFtZSI6Im5pY2siLCJ1c2VybmFtZSI6InVzZXIyQG1haWwuY29tIiwiaWF0IjoxNjYyNjA0OTE1LCJleHAiOjE2NjI2OTEzMTV9.UV1uT25YfsEdOpRBZ6oiIFxLIZ_YYbVBpG31_Fvl9u5PIgjncap-aEGunfbRdPKB2NDBk_JVccYWYQUWNvg5BQ
        Accept: application/hal+json
        Content-Length: 224
        Host: pjs.or.kr:8080

        {
        "category" : "java",
        "thumbnail" : null,
        "title" : "제목",
        "body" : "내용",
        "bodyHtml" : "내용",
        "writeTime" : "2022-08-05T14:30:00",
        "updateTime" : null,
        "hitCout" : 0,
        "replies" : null
        }


        ##### -Request header
        | Name | Description |
        |:---|:---|
        | Accept | accept header |
        | Content-Type | content type header |
        | Authorization | Bearer Token |


        ##### -Request fileds
        | Path | Type | Description |
        |:---|:---|:---|
        | title | String | 게시글의 제목 |
        | body | String | markdown 태그 형태의 게시글 본문 |
        | bodyHtml | String | 이지웍 형태의 본문 150자 > 미리보기 |
        | writeTime | String | 작성일자 |


        ##### -request body
        {
        "category" : "java",
        "thumbnail" : null,
        "title" : "제목",
        "body" : "내용",
        "bodyHtml" : "내용",
        "writeTime" : "2022-08-05T14:30:00",
        "updateTime" : null,
        "hitCout" : 0,
        "replies" : null
        }

        #### RESPONSE
        -HTTP response example [응답예제]
        {
        "id" : 74,
        "category" : "java",
        "title" : "제목",
        "body" : "내용",
        "bodyHtml" : "내용",
        "thumbnail" : null,
        "writeTime" : "2022-08-05 14:30:00",
        "updateTime" : null,
        "hitCout" : 0,
        "account" : {
        "id" : 73,
        "nickname" : "nick"
        },
        "replies" : null,
        "_links" : {
        "query-content" : {
        "href" : "http://localhost:8080/api/content/java"
        },
        "self" : {
        "href" : "http://localhost:8080/api/content/java/74"
        },
        "update-content" : {
        "href" : "http://localhost:8080/api/content/java/74"
        },
        "profile" : {
        "href" : "/docs/asciidoc/api.html#resources-content-create"
        }
        }
        }

        ##### -HTTP response header
        | Name | Description |
        |:---|:---|
        | Location | Location header |
        | Content-Type | HAL JSON TYPE |


        ##### -HTTP response fields
        | Path | Type | Description |
        |:---|:---|:---|
        | id | Number | 게시글의 식별자 |
        | title | String | 게시글의 식별자 |
        | body | String | 본문 마크업 |
        | bodyHtml | String | 이지웍 형태의 본문 150자 > 미리보기 |
        | writeTime | String | 작성일자 |
        | updateTime | Null | 수정일자 |
        | _links.self.href | String | 셀프 링크 |
        | _links.query-content.href | String | 조회 링크 |
        | _links.update-content.href | String | 수정 링크 |
        | _links.profile.href | String | 프로필자 |




        ### 게시글 수정
        #### REQUEST
        PUT 요청을 사용하여 서비스의 게시글을 수정할 수 있다.

        ##### -Curl request
        $ curl 'https://pjs.or.kr:8080/api/content/java/63' -i -X PUT \
        -H 'Content-Type: application/json;charset=UTF-8' \
        -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJuaWNrbmFtZSI6Im5pY2siLCJ1c2VybmFtZSI6ImFkbWluMjJAZW1haWwuY29tIiwiaWF0IjoxNjYyNjA0OTE0LCJleHAiOjE2NjI2OTEzMTR9.4q1mD3CmLvrPaqpu5fORVLS9IA2mCGGB52mgFqEL90DdJGjc5j40Piimp62_CiJXhQrPi2UA3iyV6MhbytaSTA' \
        -H 'Accept: application/hal+json' \
        -d '{
        "category" : "java",
        "thumbnail" : null,
        "title" : "수정된 제목",
        "body" : "수정된 내용",
        "bodyHtml" : "수정된 내용",
        "writeTime" : null,
        "updateTime" : "2022-09-05T14:30:00",
        "hitCout" : 0,
        "replies" : null
        }'


        ##### -HTTP request
        PUT /api/content/java/63 HTTP/1.1
        Content-Type: application/json;charset=UTF-8
        Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJuaWNrbmFtZSI6Im5pY2siLCJ1c2VybmFtZSI6ImFkbWluMjJAZW1haWwuY29tIiwiaWF0IjoxNjYyNjA0OTE0LCJleHAiOjE2NjI2OTEzMTR9.4q1mD3CmLvrPaqpu5fORVLS9IA2mCGGB52mgFqEL90DdJGjc5j40Piimp62_CiJXhQrPi2UA3iyV6MhbytaSTA
        Accept: application/hal+json
        Content-Length: 254
        Host: pjs.or.kr:8080

        {
        "category" : "java",
        "thumbnail" : null,
        "title" : "수정된 제목",
        "body" : "수정된 내용",
        "bodyHtml" : "수정된 내용",
        "writeTime" : null,
        "updateTime" : "2022-09-05T14:30:00",
        "hitCout" : 0,
        "replies" : null
        }


        ##### -Request header
        | Name | Description |
        |:---|:---|
        | Accept | accept header |
        | Content-Type | content type header |
        | Authorization | Bearer Token |


        ##### -Request fileds
        | Path | Type | Description |
        |:---|:---|:---|
        | title | String | 게시글의 제목 |
        | body | String | markdown 태그 형태의 게시글 본문 |
        | bodyHtml | String | 이지웍 형태의 본문 150자 > 미리보기 |
        | updateTime | String | 수정일자 |


        ##### -request body
        {
        "category" : "java",
        "thumbnail" : null,
        "title" : "수정된 제목",
        "body" : "수정된 내용",
        "bodyHtml" : "수정된 내용",
        "writeTime" : null,
        "updateTime" : "2022-09-05T14:30:00",
        "hitCout" : 0,
        "replies" : null
        }



        ### 게시글 삭제
        #### REQUEST
        DELETE 요청을 사용하여 서비스의 게시글을 삭제할 수 있다.

        ##### -Curl request
        $ curl 'https://pjs.or.kr:8080/api/content/java/65' -i -X DELETE \
        -H 'Content-Type: application/json;charset=UTF-8' \
        -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJuaWNrbmFtZSI6Im5pY2siLCJ1c2VybmFtZSI6ImFkbWluMjNAZW1haWwuY29tIiwiaWF0IjoxNjYyNjA0OTE1LCJleHAiOjE2NjI2OTEzMTV9.hafmAhjYdPyEkXsY0fNQWaXLDkc8YG1-Wowi9Sxea3Kcby-q5lKRq2pABvOQpa7LZzWGLvnr4v3AAiVaaVcLRA' \
        -H 'Accept: application/hal+json'
        ##### -HTTP request
        DELETE /api/content/java/65 HTTP/1.1
        Content-Type: application/json;charset=UTF-8
        Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9BRE1JTixST0xFX1VTRVIiLCJuaWNrbmFtZSI6Im5pY2siLCJ1c2VybmFtZSI6ImFkbWluMjNAZW1haWwuY29tIiwiaWF0IjoxNjYyNjA0OTE1LCJleHAiOjE2NjI2OTEzMTV9.hafmAhjYdPyEkXsY0fNQWaXLDkc8YG1-Wowi9Sxea3Kcby-q5lKRq2pABvOQpa7LZzWGLvnr4v3AAiVaaVcLRA
        Accept: application/hal+json
        Host: pjs.or.kr:8080

        ##### -Request header
        | Name | Description |
        |:---|:---|
        | Accept | accept header |
        | Content-Type | content type header |
        | Authorization | Bearer Token |
