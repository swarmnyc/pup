<?php

	/**
	 * Utility methods for cleaning up form input (to prevent SQL injection).
	 *
	 * Version History:
	 *
	 * 08/19/2010		NV		Added method to secure all request variables at once.
	 * 08/18/2010		NV		Initial version.
	 */

	// Method courtesy http://php.net/manual/en/security.database.sql-injection.php
	function secure_clean($value)
	{
	    	$temp = $value;
	    	//$temp = urldecode($temp);
	    	
	        $temp = htmlspecialchars(stripslashes($value));
	        $temp = str_ireplace("<script", "%3Cscript", $temp);
	        $temp = mysql_escape_string($temp);
	        return $temp;
    }


    function secure_forms() {
    	foreach ($_REQUEST as $key=>$value) {
    		$_REQUEST[$key] = secure_clean($value);
    	}
    }