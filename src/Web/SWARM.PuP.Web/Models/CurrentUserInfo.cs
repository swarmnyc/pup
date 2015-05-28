﻿using System.Collections.Generic;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.ViewModels
{
    public class CurrentUserInfo : UserInfo
    {
        public CurrentUserInfo(PuPUser user)
        {
            this.Id = user.Id;

            this.UserName = user.UserName;

            this.Email = user.Email;

            this.Tags = user.Tags;

            this.PortraitUrl = user.PortraitUrl;
        }

        public string Email { get; set; }
        
        public IEnumerable<PuPTag> Tags { get; set; }
    }

    public class CurrentUserToken : CurrentUserInfo
    {
        public CurrentUserToken(PuPUser user) : base(user)
        {

        }

        public string AccessToken { get; set; }

        public long ExpiresIn { get; set; }
    }
}