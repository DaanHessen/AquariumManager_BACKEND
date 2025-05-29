package nl.hu.bep.application.mapper;

import jakarta.inject.Inject;
import nl.hu.bep.domain.*;
import nl.hu.bep.presentation.dto.*;

import java.util.List;

public class EntityMappingService {
    
    private final AquariumMapper mapper;
    
    @Inject
    public EntityMappingService(AquariumMapper mapper) {
        this.mapper = mapper;
    }
    
    public AquariumResponse mapAquarium(Aquarium aquarium) {
        return mapper.toAquariumResponse(aquarium, false);
    }
    
    public AquariumResponse mapAquariumDetailed(Aquarium aquarium) {
        return mapper.toAquariumResponse(aquarium, true);
    }

    public List<AquariumResponse> mapAquariums(List<Aquarium> aquariums) {
        return mapper.toAquariumResponseList(aquariums, false);
    }
    
    public List<AquariumResponse> mapAquariumsDetailed(List<Aquarium> aquariums) {
        return mapper.toAquariumResponseList(aquariums, true);
    }

    public InhabitantResponse mapInhabitant(Inhabitant inhabitant) {
        return mapper.toInhabitantResponse(inhabitant);
    }
    
    public List<InhabitantResponse> mapInhabitants(List<Inhabitant> inhabitants) {
        return inhabitants.stream()
            .map(mapper::toInhabitantResponse)
            .toList();
    }

    public AccessoryResponse mapAccessory(Accessory accessory) {
        return mapper.toAccessoryResponse(accessory);
    }

    public List<AccessoryResponse> mapAccessories(List<Accessory> accessories) {
        return accessories.stream()
            .map(mapper::toAccessoryResponse)
            .toList();
    }

    public OrnamentResponse mapOrnament(Ornament ornament) {
        return mapper.toOrnamentResponse(ornament);
    }
 
    public List<OrnamentResponse> mapOrnaments(List<Ornament> ornaments) {
        return ornaments.stream()
            .map(mapper::toOrnamentResponse)
            .toList();
    }

    public AquariumStateHistoryResponse mapStateHistory(AquariumStateHistory stateHistory) {
        return mapper.toStateHistoryResponse(stateHistory);
    }

    public List<AquariumStateHistoryResponse> mapStateHistories(List<AquariumStateHistory> stateHistories) {
        return stateHistories.stream()
            .map(mapper::toStateHistoryResponse)
            .toList();
    }
} 