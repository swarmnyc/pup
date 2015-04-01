using System;
using System.Collections.Generic;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.ViewModels
{
    public class LobbyViewModel
    {
        private Lobby lobby;

        public LobbyViewModel(Lobby lobby)
        {
            this.lobby = lobby;
        }

        public string Id
        {
            get { return lobby.Id; }
        }

        public string GameId {
            get { return lobby.GameId; }
        }

        public string Name
        {
            get { return lobby.Name; }
        }

        public string PictureUrl
        {
            get { return lobby.PictureUrl; }
        }

        public string Description
        {
            get { return lobby.Description; }
        }

        public PlayStyle PlayStyle
        {
            get { return lobby.PlayStyle; }
        }

        public SkillLevel SkillLevel
        {
            get { return lobby.SkillLevel; }
        }

        public DateTime StartTimeUtc
        {
            get { return lobby.StartTimeUtc; }
        }

        public static IEnumerable<LobbyViewModel> Load(IEnumerable<Lobby> lobbies)
        {
            foreach (var lobby in lobbies)
            {
                yield return new LobbyViewModel(lobby);
            }
        }
    }
}