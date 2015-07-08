using System.Collections.Generic;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.ViewModels {
    public class LobbySearchResult {
        public int[] Counts { get; set; }
        public IEnumerable<Lobby> Result { get; set; }
    }
}