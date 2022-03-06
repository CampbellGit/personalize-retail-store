package com.daveo.retailapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.personalizeruntime.model.GetPersonalizedRankingRequest;
import software.amazon.awssdk.services.personalizeruntime.model.GetPersonalizedRankingResponse;
import software.amazon.awssdk.services.personalizeruntime.model.PersonalizeRuntimeException;
import software.amazon.awssdk.services.personalizeruntime.model.PredictedItem;

public class RankingService {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetPersonalizedRanking <campaignArn> <userId> <items>\n\n" +
                "Where:\n" +
                "    campaignArn - The ARN of the campaign.\n" +
                "    userId - The user ID to provide recommendations for.\n" +
                "    itemList - A comma delimited list of items to rank for the user\n\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String campaignArn = args[0];
        String userId = args[1];
        ArrayList<String> items = new ArrayList<>(Arrays.asList(args[2].split("\\s*,\\s*")));
        Region region = Region.US_EAST_1;
        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();

        getRankedRecs(personalizeRuntimeClient, campaignArn, userId, items);
        personalizeRuntimeClient.close();
    }

    public static List<PredictedItem> getRankedRecs(PersonalizeRuntimeClient personalizeRuntimeClient,
                                                    String campaignArn,
                                                    String userId,
                                                    ArrayList<String> items) {

        try {
            GetPersonalizedRankingRequest rankingRecommendationsRequest = GetPersonalizedRankingRequest.builder()
                    .campaignArn(campaignArn)
                    .userId(userId)
                    .inputList(items)
                    .build();

            GetPersonalizedRankingResponse recommendationsResponse =
                    personalizeRuntimeClient.getPersonalizedRanking(rankingRecommendationsRequest);
            List<PredictedItem> rankedItems = recommendationsResponse.personalizedRanking();
            int rank = 1;
            for (PredictedItem item : rankedItems) {
                System.out.println("Item ranked at position " + rank + " details");
                System.out.println("Item Id is : " + item.itemId());
                System.out.println("Item score is : " + item.score());
                System.out.println("---------------------------------------------");
                rank++;
            }
            return rankedItems;
        } catch (PersonalizeRuntimeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
}