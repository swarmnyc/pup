<?php defined('ABSPATH') or die("Crap ! You can not access this directly.");
/**
* This page contains all major custom functions used in DCL
* Category	:	Functions
* Author	:	Joel James
* Created On:	20/10/2014
*/

	 
	/**
	 * Loads div to load comments
	 * This will be used if the shortcode is enabled
	 * @return $js_div
	 **/
	function add_js_disqus_div($js_div) {
	
		if (get_option('js_shortcode') == 'yes') {
		if (get_option('js_button')) {
			$js_button = get_option('js_button');
		}
		else
		{
			$js_button = 'Load Comments';
		}
        $js_type = get_option('js_type');
        if ($js_type == 'click') {
            $js_class = get_option('js_class');
            $js_div .= "<div id='disqus_thread'>
						<div id='hidden-div' align='center'>
						<button style='margin-bottom:20px;' id='js_comment_div' class='".$js_class."'>".$js_button."</button>
						</div></div>";
        }
		else if ($js_type = 'scroll') {
						$js_div .= "<div id='disqus_thread'></div>";
        }
        return $js_div;
		}
	}

	
	
	/**
	* Disqus comments js code
	*/
	function js_disqus_conditional_code(){
	
			$scroll = 'jQuery(function () 
						{
							var disqus_div = jQuery("#disqus_thread");
							if (disqus_div.size() > 0) 
							{
								jQuery(window).scroll(loadDisqus);      
							}  
						}
						);';
			$click = 'jQuery(function () {
						jQuery("#js_comment_div").click(loadDisqus);
						});';
			
			$condition = (get_option('js_type') == 'scroll') ? $scroll : $click;
			
			$script = '<script type="text/javascript">
						var disqus_shortname = "'.strtolower(get_option("disqus_forum_url")).'";
						if (typeof ds_loaded == "undefined") {
							var ds_loaded = false; //To track loading only once on a page.
						}
						function loadDisqus()
						{
							var disqus_div = jQuery("#disqus_thread"); //The ID of the Disqus DIV tag
							var top = disqus_div.offset().top;
							var disqus_data = disqus_div.data();
							if ( !ds_loaded && jQuery(window).scrollTop() + jQuery(window).height() > top ) 
							{
								ds_loaded = true;
								for (var key in disqus_data) 
								{
									if (key.substr(0,6) == "disqus") 
									{
										window["disqus_" + key.replace("disqus","").toLowerCase()] = disqus_data[key];
									}
								}
								var dsq = document.createElement("script");
								dsq.type = "text/javascript";
								dsq.async = true;
								dsq.src = "//" + window.disqus_shortname + ".disqus.com/embed.js";
								jQuery("#hidden-div").html("<h4>'.get_option('js_message').'</h4>");
								(document.getElementsByTagName("head")[0] || document.getElementsByTagName("body")[0]).appendChild(dsq);
							}    
						}
						';
			$script .= $condition;		
			$script .= '</script>';
		
		echo $script;
	 }
	 
	 /*
	 * Normal Disqus code without lazy load
	 */
	 function js_normal_conditional_code() {
		$script = '';
		$script = "<script type='text/javascript'>
					/* <![CDATA[ */
					(function() {
					var dsq = document.createElement('script'); dsq.type = 'text/javascript';
						var disqus_shortname = '".strtolower(get_option('disqus_forum_url'))."';
						dsq.async = true;
						dsq.src = '//' + disqus_shortname + '.' + '".DISQUS_DOMAIN."' + '/' + 'embed' + '.js' + '?pname=wordpress&pver=<?php echo DISQUS_VERSION; ?>';
						(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
					})();
					/* ]]> */
					</script>";
		echo $script;
	 }
	 
	 /*
	 * Function to laod comments if comments url
	 */
	 function js_comments_hash_load() {
		$script = "";
		$script = "<script type='text/javascript'>
					/* <![CDATA[ */
					var hash = window.location.hash;
					if(hash!==''){
					var ds_loaded = true;
					(function() {
						var dsq = document.createElement('script'); dsq.type = 'text/javascript';
						dsq.async = true;
						dsq.src = '//' + disqus_shortname + '.' + '".DISQUS_DOMAIN."' + '/' + 'embed' + '.js' + '?pname=wordpress&pver=<?php echo DISQUS_VERSION; ?>';
						(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
						
					})();}
					/* ]]> */
					</script>";
		echo $script;
		
	 }
	 
	 /*
	 * Fixing conflict with woocommerce review system
	 * 
	 * @since 9.0.4
	 * @thanks : http://danieljlewis.net/how-to-disable-disqus-on-wordpress-custom-post-types/
	 */
		
		add_filter( 'comments_template' , 'js_block_disqus', 1 );
		function js_block_disqus($file) {
			if ( 'product' == get_post_type() )
				remove_filter('comments_template', 'dsq_comments_template');
			return $file;
		}