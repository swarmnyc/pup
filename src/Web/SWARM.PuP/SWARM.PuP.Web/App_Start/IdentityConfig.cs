using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using System.Web;
using AspNet.Identity.MongoDB;
using Autofac;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using Microsoft.Owin;
using Microsoft.Owin.Security;
using Microsoft.Owin.Security.Cookies;
using Owin;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web
{
    public class EmailService : IIdentityMessageService
    {
        public Task SendAsync(IdentityMessage message)
        {
            // Plug in your email service here to send an email.
            return Task.FromResult(0);
        }
    }

    public class SmsService : IIdentityMessageService
    {
        public Task SendAsync(IdentityMessage message)
        {
            // Plug in your SMS service here to send a text message.
            return Task.FromResult(0);
        }
    }

    // Configure the application user manager used in this application. UserManager is defined in ASP.NET Identity and is used by the application.
    public class PuPUserManager : UserManager<PuPUser>
    {
        private IChatService chatService;
        
        private PuPUserManager(UserStore<PuPUser> store, IChatService chatService) : base(store)
        {
            this.chatService = chatService;
        }

        public static PuPUserManager Create(IdentityFactoryOptions<PuPUserManager> options, IOwinContext context)
        {   
            IChatService chatService = PuPApplication.Container.Resolve<IChatService>();
            var manager = new PuPUserManager(new UserStore<PuPUser>(context.Get<PuPIdentityContext>()), chatService);
            // Configure validation logic for usernames
            manager.UserValidator = new UserValidator<PuPUser>(manager)
            {
                AllowOnlyAlphanumericUserNames = false,
                RequireUniqueEmail = true
            };

            // Configure validation logic for passwords
            manager.PasswordValidator = new PasswordValidator
            {
                RequiredLength = 6,
                RequireNonLetterOrDigit = false,
                RequireDigit = true,
                RequireLowercase = false,
                RequireUppercase = false,
            };

            // Configure user lockout defaults
            manager.UserLockoutEnabledByDefault = true;
            manager.DefaultAccountLockoutTimeSpan = TimeSpan.FromMinutes(5);
            manager.MaxFailedAccessAttemptsBeforeLockout = 5;

            // Register two factor authentication providers. This application uses Phone and Emails as a step of receiving a code for verifying the user
            // You can write your own provider and plug it in here.
            manager.RegisterTwoFactorProvider("Phone Code", new PhoneNumberTokenProvider<PuPUser>
            {
                MessageFormat = "Your security code is {0}"
            });
            manager.RegisterTwoFactorProvider("Email Code", new EmailTokenProvider<PuPUser>
            {
                Subject = "Security Code",
                BodyFormat = "Your security code is {0}"
            });
            manager.EmailService = new EmailService();
            manager.SmsService = new SmsService();
            var dataProtectionProvider = options.DataProtectionProvider;
            if (dataProtectionProvider != null)
            {
                manager.UserTokenProvider = 
                    new DataProtectorTokenProvider<PuPUser>(dataProtectionProvider.Create("ASP.NET Identity"));
            }
            return manager;
        }

        public override Task<IdentityResult> CreateAsync(PuPUser user)
        {
            this.chatService.CreateUser(user);
            return base.CreateAsync(user);
        }
    }

    // Configure the application sign-in manager which is used in this application.
    public class PuPSignInManager : SignInManager<PuPUser, string>
    {
        public PuPSignInManager(PuPUserManager userManager, IAuthenticationManager authenticationManager)
            : base(userManager, authenticationManager)
        {
        }

        public override Task<ClaimsIdentity> CreateUserIdentityAsync(PuPUser user)
        {
            return UserManager.CreateIdentityAsync(user, DefaultAuthenticationTypes.ApplicationCookie);
        }

        public static PuPSignInManager Create(IdentityFactoryOptions<PuPSignInManager> options, IOwinContext context)
        {
            return new PuPSignInManager(context.GetUserManager<PuPUserManager>(), context.Authentication);
        }
    }
}
