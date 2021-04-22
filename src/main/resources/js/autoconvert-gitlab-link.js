/**
 * Autoconvert a GitLab link into a macro
 *
 * @see https://developer.atlassian.com/server/confluence/extending-autoconvert/
 */
define('org/vaulttec/gitlab-links', ['tinymce', 'ajs'], function(tinymce, AJS) {
	"use strict";

	function GitLabLinks() {
		let config;
		AJS.$.getJSON(AJS.contextPath() + "/rest/api/latest/config", function(data) {
			config = data;
		});

		function pasteHandler(uri, node, done) {
			if (uri.host === config.serverHost) {
				let directoryParts = uri.directory.split('/');

				// File URL, e.g. https://gitlab.com/gitlab-org/gitlab/-/blob/master/VERSION
				if (directoryParts.length >= 7 && directoryParts[3] === "-" &&
					directoryParts[4] === "blob") {
					let macro = {
						'name': 'code-block',
						'params': { 'url': uri.protocol + '://' + uri.host + uri.path, 'theme': config.defaultTheme }
					};
					if (uri.anchor.startsWith('L')) {
						let lines = uri.anchor.substring(1).split("-");
						macro.params.firstLine = lines[0];
						if (lines.length > 1) {
							macro.params.lastLine = lines[1];
						}
					}
					tinymce.plugins.Autoconvert.convertMacroToDom(macro, done, done);
				} else {
					done();
				}
			} else {
				done();
			}
		}
		tinymce.plugins.Autoconvert.autoConvert.addHandler(pasteHandler);
	}

	return GitLabLinks;
});

require('confluence/module-exporter').safeRequire('org/vaulttec/gitlab-links', function(GitLabLinks) {
	require('ajs').bind("init.rte", GitLabLinks);
});
