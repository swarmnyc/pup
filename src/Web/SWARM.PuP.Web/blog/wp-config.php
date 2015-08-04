<?php
/**
 * The base configurations of the WordPress.
 *
 * This file has the following configurations: MySQL settings, Table Prefix,
 * Secret Keys, and ABSPATH. You can find more information by visiting
 * {@link http://codex.wordpress.org/Editing_wp-config.php Editing wp-config.php}
 * Codex page. You can get the MySQL settings from your web host.
 *
 * This file is used by the wp-config.php creation script during the
 * installation. You don't have to use the web site, you can just copy this file
 * to "wp-config.php" and fill in the values.
 *
 * @package WordPress
 */

// ** MySQL settings - You can get this info from your web host ** //
/** The name of the database for WordPress */
define('DB_NAME', 'theyarea_pupbog');

/** MySQL database username */
define('DB_USER', 'theyarea_pup');

/** MySQL database password */
define('DB_PASSWORD', 'kwR^e-{;z+ln');

/** MySQL hostname */
define('DB_HOST', 'localhost');

/** Database Charset to use in creating database tables. */
define('DB_CHARSET', 'utf8');

/** The Database Collate type. Don't change this if in doubt. */
define('DB_COLLATE', '');

/**#@+
 * Authentication Unique Keys and Salts.
 *
 * Change these to different unique phrases!
 * You can generate these using the {@link https://api.wordpress.org/secret-key/1.1/salt/ WordPress.org secret-key service}
 * You can change these at any point in time to invalidate all existing cookies. This will force all users to have to log in again.
 *
 * @since 2.6.0
 */
define('AUTH_KEY',         'L@{U|lt]e21$>q|Mf-m|iNt5Wq/ge>1l4ooHU_NM1}q,;Sx{`<%yo)T5u|-ubB+{');
define('SECURE_AUTH_KEY',  '~vVWvCNovsFcLxK<s_x]-{NCJC>m+|HPOF#jA% =Y,guJ{YPU5CO~L@KCC*ARIq$');
define('LOGGED_IN_KEY',    'sQDGLbkGTS-UkFv/1U9_i%K nv- RlpYh)B|qq~-2R)/5/x5cwfL-h_tk=_J=CxG');
define('NONCE_KEY',        'GD=m:Bd`hm$wke$>#l{-CRu5>?r=^y->3UK~O;{`,QQdE|l~729BvoH0vucT^i8Y');
define('AUTH_SALT',        'od(-sYqR0z!Mf^`uK-,`c|;C.oIdxK)&94eL7!F6_|kJq43X_(|aiY&2w=NVD[Y`');
define('SECURE_AUTH_SALT', '|#q&)V.qxR4a]LL,s4p]8vGag+e4u<XkO?:wZ{-|:qdeEQy9[=$tm^7|G.)+@-fx');
define('LOGGED_IN_SALT',   'gX]3jU5BONXbHqEqW9<yghy+|]d+Jn/)}l#]1#^m]T?Ay+$L`W,BfG{-qb#C)dsv');
define('NONCE_SALT',       ';H.QC*pm+}#}P5zm(Db- GfB}W@Kx*<xqo{KO#fLh*3vFSbmexTTLdyQm|-J[u-k');

/**#@-*/

/**
 * WordPress Database Table prefix.
 *
 * You can have multiple installations in one database if you give each a unique
 * prefix. Only numbers, letters, and underscores please!
 */
$table_prefix  = 'wp_';

/**
 * For developers: WordPress debugging mode.
 *
 * Change this to true to enable the display of notices during development.
 * It is strongly recommended that plugin and theme developers use WP_DEBUG
 * in their development environments.
 */
define('WP_DEBUG', false);

/* That's all, stop editing! Happy blogging. */

/** Absolute path to the WordPress directory. */
if ( !defined('ABSPATH') )
	define('ABSPATH', dirname(__FILE__) . '/');

/** Sets up WordPress vars and included files. */
require_once(ABSPATH . 'wp-settings.php');
