if (typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function(str) {
		return this.slice(0, str.length) == str;
	};
}
$.noConflict();

jQuery(document).ready(
		function() {
			jQuery("a").each(
					function() {
						var link = jQuery(this).attr("href");
					
						if(link.startsWith("../"))
							link = link.substring(3,link.length);
							
							if(link.startsWith(contextPath))
							{
								link = link.substring(contextPath.length+1,link.length);
							}
							if(link.indexOf("?")!=-1)
							{
								link= link.substring(0,link.indexOf("?"));
							}
						var thisLink = this;
						
						var i;
						for (i = 0; i < notGrantedPermissions.length; i++) {
							var temp = notGrantedPermissions[i].substring(2,notGrantedPermissions[i].length - 1);
							if (link==temp) {
								jQuery(this).remove();
							}
						}
					});
		});
