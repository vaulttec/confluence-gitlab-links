You have successfully created an Atlassian Plugin!

Here are the SDK commands you'll use immediately:

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-help  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK



# API calls

* Confluence: Get [username of authenticated Confluence user](https://developer.atlassian.com/server/confluence/how-do-i-find-the-logged-in-user/#confluence-5-2-and-above) via `AuthenticatedUserAccessor.get()`
* GitLab: Authentication as specific user via [sudo](https://docs.gitlab.com/ee/api/#sudo) via `&sudo=<username>`
* GitLab: Get [raw content for URL-encoded path of the project and file](https://docs.gitlab.com/ee/api/repository_files.html#get-raw-file-from-repository)   via `GET /projects/<URL-encoded project path>/repository/files/<URL-encode file path>/raw`
