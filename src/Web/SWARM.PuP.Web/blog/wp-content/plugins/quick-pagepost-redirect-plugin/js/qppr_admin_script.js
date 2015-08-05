;(function($){
	$(document).ready(function() {
		$('span.qppr_meta_help').css('display','none');
		$('.inside').delegate('span.qppr_meta_help_wrap', 'hover', function(e){
			var $curdisp = $(this).find('span.qppr_meta_help').css('display');
			if($curdisp == 'none'){
				$(this).find('span.qppr_meta_help').css('display','inline');
			}else{
				$(this).find('span.qppr_meta_help').css('display','none');
			}
			e.preventDefault();
		});
		var mainurl = qpprData.ajaxurl; 
		$( '#pprredirect_type').on( 'change', function(e){
			e.preventDefault();
			$( '.qppr-meta-section-wrapper' ).removeClass( 'meta-selected meta-not-selected' );
			var selVal = $( this ).val();
			if( selVal == 'meta' ){
				$( '.qppr-meta-section-wrapper' ).slideDown( 'slow' );
			}else{
				$( '.qppr-meta-section-wrapper' ).slideUp( 'slow' );
			}
		});
		$( '.qppr-delete-regular' ).on( 'click', function(e){
			e.preventDefault();
			if( confirm( qpprData.msgIndividualDeleteConfirm ) ){ 
				var remove_qppr_all_indiviual_data = {'action' : 'qppr_delete_all_iredirects', 'security': qpprData.securityDelete};
				$.post(qpprData.ajaxurl, remove_qppr_all_indiviual_data, function( response ) {
					if( response == 'success' ){
						document.location.href = qpprData.adminURL+'?page=redirect-options&update=2';
					}else{
						document.location.href = qpprData.adminURL+'?page=redirect-options&update=0';
					}
				});
			}
		});
		
		$( '.qppr-delete-quick' ).on( 'click', function(e){
			e.preventDefault();
			if( confirm( qpprData.msgQuickDeleteConfirm ) ){ 
				var remove_qppr_all_quick_data = {'action' : 'qppr_delete_all_qredirects', 'security': qpprData.securityDelete};
				$.post(qpprData.ajaxurl, remove_qppr_all_quick_data, function(response) {
					if( response == 'success' ){
						document.location.href = qpprData.adminURL+'?page=redirect-options&update=3';
					}else{
						document.location.href = qpprData.adminURL+'?page=redirect-options&update=0';
					}
				});
			}
		});

		$( '#qppr_quick_save_form' ).on( 'submit', function(e){
			var obj = $( this );
			var reqs = $('input[name^="quickppr_redirects[request]"');
			var dest = $('input[name^="quickppr_redirects[destination]"');
			var err  = false;
			if( reqs[0].value == '' && dest[0].value == '' ){err  = true;}
			if( err ){
				e.preventDefault();
				alert(qpprData.error);
				return false;
			}
			return true;
		});
		
		$(".delete-qppr").click(function(e){ 
			e.preventDefault(); 
			var rowID 	= $(this).data('rowid');
			var delRow	= $('#'+rowID); 
			var request = delRow.children('.table-qppr-req').children('.qppr-request').text();
			if( confirm( qpprData.msgDeleteConfirm ) ){ 
				var remove_qppr_data = {'action' : 'qppr_delete_quick_redirect','request': request,'security': qpprData.security};
				$.post(qpprData.ajaxurl, remove_qppr_data, function(response) {
					delRow.remove(); 
				}).done(function() {
				});
			}
		}); 
		$(".edit-qppr").click(function(e){ 
			e.preventDefault(); 
			var rowID = $(this).data('rowid');
			var currentRedirect	= $('#'+rowID); 
			var editRowHolder 	= $('#qppr-edit-row-holder').children('td'); 
			$( '#' + rowID + ' td' ).addClass('editing');
			currentRedirect.addClass('editing-redirect');
			editRowHolder.clone().prependTo(currentRedirect);
			currentRedirect.children('.table-qppr-req.cloned').children('.input-qppr-req').attr('value', currentRedirect.children('.table-qppr-req.editing').children('.qppr-request').text());
			currentRedirect.children('.table-qppr-des.cloned').children('.input-qppr-dest').attr('value', currentRedirect.children('.table-qppr-des.editing').children('.qppr-destination').text());
			var newChecked = currentRedirect.children('.table-qppr-nwn.editing').children( '.qppr-newindow' ).text() == 'X' ? true : false ; 
			currentRedirect.children('.table-qppr-nwn.cloned').children('.input-qppr-neww').prop( 'checked', newChecked );
			var noChecked = currentRedirect.children('.table-qppr-nfl.editing').children( '.qppr-nofollow' ).text() == 'X' ? true : false ; 
			currentRedirect.children('.table-qppr-nfl.cloned').children('.input-qppr-nofo').prop( 'checked', noChecked );
			currentRedirect.children('.table-qppr-sav.cloned').children('.table-qppr-sav span').attr('data-rowid',rowID);
			currentRedirect.children('.table-qppr-can.cloned').children('.table-qppr-can span').attr('data-rowid',rowID);
		}); 
		$(".qppr_quick_redirects_wrapper").delegate('.table-qppr-sav span.qpprfont-save', 'hover', function(e){
			if( $( '.active-saving' ).length != 0 && !$( this ).parent().parent().hasClass('active-saving'))
				$( this ).css( {'cursor':'no-drop','color':'#ff0000'} );
		});

		$(".qppr_quick_redirects_wrapper").delegate('.table-qppr-sav span.qpprfont-save', 'click', function(e){
			e.preventDefault();
			var editRow 	= $('#'+$(this).data('rowid'));
			if( $( '.active-saving' ).length != 0 && !$( this ).parent().parent().hasClass('active-saving'))
				return false;
			var rowID 		= $(this).data('rowid');
			var request 	= editRow.children('.table-qppr-req.cloned').children('.input-qppr-req').val();
			var destination = editRow.children('.table-qppr-des.cloned').children('.input-qppr-dest').val();
			var newWin 		= editRow.children('.table-qppr-nwn.cloned').children('.input-qppr-neww:checked').val();
			var noFoll 		= editRow.children('.table-qppr-nfl.cloned').children('.input-qppr-nofo:checked').val();
			newWin = (typeof newWin == 'undefined' || newWin == 'undefined') ? 0 : newWin;
			noFoll = (typeof noFoll == 'undefined' || noFoll == 'undefined') ? 0 : noFoll;
			editRow.children('.cloned').remove();
			// do a little checking of the request to make sure is ok.
			var protocols	= qpprData.protocols;
			var slash 		= request.substring(0, 1);
			var hasSlash 	= slash ==  '/' ? true : false;
			var protocol	= '';
			var protoLen	= -1;
			if( !hasSlash ){
				protoLen	= request.indexOf(":");
				protocol	= request.substring(0, protoLen); // first three of protocol
			}
			if( !hasSlash && $.inArray( protocol, protocols ) === -1 ){
				request = '/' + request;
			}

			$( '#qppr-edit-row-saving .qppr-saving-row' ).clone().prependTo( '#' + rowID );
			editRow.addClass( 'active-saving' );
			var save_data 	= {
				'action' 		: 'qppr_save_quick_redirect', 
				'row'			: rowID.replace('rowpprdel-',''), 
				'request'		: request,
				'destination'	: destination,
				'newwin'		: newWin,
				'nofollow'		: noFoll,
				'security'		: qpprData.security
			};
			
			$.post(qpprData.ajaxurl, save_data, function(response) {
				var err = 0;
				if( response == 'error' ){
					alert(qpprData.msgErrorSave);
					err = 1;
				}
				if(response == 'duplicate' ){
					alert(qpprData.msgDuplicate);
					var dupRow = '#' + $( ".table-qppr-req:contains(" + request + ")" ).parent('tr').attr('id');
					$( dupRow ).addClass('qppr-duplicate');
					err = 1;
				}
				if(	err != 1 ){
				if(noFoll == 1){noFoll = 'X';}else{noFoll = '';}
				if(newWin == 1){newWin = 'X';}else{newWin = '';}
					editRow.children('.table-qppr-req.editing').children('.qppr-request').text(request);
					editRow.children('.table-qppr-des.editing').children('.qppr-destination').text(destination);
					editRow.children('.table-qppr-nfl.editing').children('.qppr-nofollow').text(noFoll);
					editRow.children('.table-qppr-nwn.editing').children('.qppr-newindow').text(newWin);
				}
				editRow.children('td').removeClass('editing');
				editRow.children('.qppr-saving-row').remove();
			}).done(function() {
				editRow.removeClass('editing-redirect active-saving');
				$( '.table-qppr-sav span.qpprfont-save' ).css( {'cursor':'','color':''} );
			});
		});
		$('tr[id^="rowpprdel"]').on('hover',function(){
			$(this).removeClass('qppr-duplicate');
		});
		$(".qppr_quick_redirects_wrapper").delegate('.table-qppr-can span.qpprfont-cancel','click', function(e){
			e.preventDefault();
			var rowID = $('#'+$(this).data('rowid'));
			rowID.children('.cloned').remove();
			rowID.children('td').removeClass('editing');
			rowID.removeClass('editing-redirect');
		});
		
		$("#hidepprjqmessage").click(function(e){
			e.preventDefault(); 
			var pprhidemessage_data = {'action' : 'qppr_pprhidemessage_ajax','pprhidemessage': 1,'scid': qpprData.security};
			$.post(qpprData.ajaxurl, pprhidemessage_data, function(response) {$('#usejqpprmessage').remove();}).done(function() {});
		});
		 
		$("#hidepprjqmessage2").click(function(e){ 
			e.preventDefault(); 
			var pprhidemessage_data = {'action' : 'qppr_pprhidemessage_ajax','pprhidemessage': 2,'scid': qpprData.security};
			$.post(qpprData.ajaxurl, pprhidemessage_data, function(response) {$('#usejqpprmessage2').remove();}).done(function() {});
		}); 
		
		$("#qppr-import-quick-redirects-button").click(function(e){
			e.preventDefault();
			$('#qppr_addto_form').css({'display':'none'});
			if($('#qppr_import_form').css('display')=='block'){
				$('#qppr_import_form').css({'display':'none'});
			}else{
				$('#qppr_import_form').css({'display':'block'});
			}
		});
		
		$("#qppr_addto_qr_button").click(function(e){
			$('#qppr_import_form').css({'display':'none'});
			if($('#qppr_addto_form').css('display')=='block'){
				$('#qppr_addto_form').css({'display':'none'});
			}else{
				$('#qppr_addto_form').css({'display':'block'});
			}
			e.preventDefault();
		});
		
		$("#import_redirects_add_qppr").click(function(e){
			if($("[name|=qppr_file_add]").attr('value')==''){
				e.preventDefault();
				alert(qpprData.msgSelect);
				return false;
			}
		});
		
		$("#import-quick-redrects-file").click(function(e){
			if($("[name|=qppr_file]").attr('value')==''){
				e.preventDefault();
				alert(qpprData.msgSelect);
				return false;
			}
		});
	});
})(jQuery);

function qppr_check_file(fname){
	str		= fname.value.toUpperCase();
	suffix	= ".TXT";
	if(!(str.indexOf(suffix, str.length - suffix.length) !== -1)){
		alert( qpprData.msgFileType );
		fname.value	= '';
	}
}

function qppr_goOnConfirm(message, href) {
	if( confirm( message ) ){
		document.location.href = qpprData.adminURL+href;
	}
}