using System.ComponentModel.DataAnnotations;
using System.Web;
using MultipartDataMediaFormatter.Infrastructure;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.ViewModels
{
    public class LoginViewModel
    {
        [Required]
        [Display(Name = "Email")]
        [EmailAddress]
        public string Email { get; set; }

        [Required]
        [DataType(DataType.Password)]
        [Display(Name = "Password")]
        public string Password { get; set; }

        [Display(Name = "Remember me?")]
        public bool RememberMe { get; set; }
    }

    public class RegisterViewModel
    {
        [Required]
        [Display(Name = "UserName")]
        public string UserName { get; set; }

        [Required]
        [Display(Name = "Email")]
        [MaxLength(100)]
        [EmailAddress]
        public string Email { get; set; }

        [Required]
        [DataType(DataType.Password)]
        [MinLength(4)]
        [MaxLength(100)]
        [Display(Name = "Password")]
        public string Password { get; set; }
        
        public HttpFile Portrait { get; set; }

        [Display(Name = "Remember me?")]
        public bool RememberMe { get; set; }
        
        public DevicePlatform Platform { get; set; }
        
        /// <summary>
        /// This token is for Push Notification 
        /// </summary>
        public string DeviceToken { get; set; }
    }

    public class ExternalLoginViewModel
    {
        [Required]
        public string Token { get; set; }
        [Required]
        public string Provider { get; set; }
        [Required]
        public string Email { get; set; }
    }

    public class GoogleUserInfo
    {
        public string id;
        public string name;
        public string picture;
    }

    public class UserRegisterViewModel
    {
        public string UserName { get; set; }
        public string PictureUrl { get; set; }
        public string IdFromProvider { get; set; }
        public string Provider { get; set; }
        public string Email { get; set; }
    }
}
