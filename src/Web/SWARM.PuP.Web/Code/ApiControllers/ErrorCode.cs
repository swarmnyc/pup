using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace SWARM.PuP.Web.ApiControllers
{
    public class ErrorCode
    {
        public const string E001WrongParameter = "001 Wrong paramerters";
        public const string E002Exist = "002 Exist";
        public const string E003NotFound = "003 NotFound";
        public const string E003NotFoundGame = "003 NotFound.Game";
        public const string E003NotFoundLobby = "003 NotFound.Lobby";
        public const string E003NotFoundUser = "003 NotFound.User";
    }
}