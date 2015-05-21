using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
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

        [Display(Name = "Remember me?")]
        public bool RememberMe { get; set; }
    }

    public class UserInfoViewModel
    {

        public UserInfoViewModel(PuPUser user)
        {
            this.Id = user.Id;

            this.DisplayName = user.UserName;

            this.Email = user.Email;

            this.Tags = user.Tags;
        }

        public string Id { get; set; }

        public string Email { get; set; }

        public string DisplayName { get; set; }

        public string PictureUrl { get; set; }

        public IEnumerable<PuPTag> Tags { get; set; }
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

    public class UserLoggedInViewModel : UserInfoViewModel
    {
        public UserLoggedInViewModel(PuPUser user) : base(user)
        {

        }

        public string AccessToken { get; set; }

        public string TokenType { get; set; }

        public double ExpiresIn { get; set; }
    }
}
